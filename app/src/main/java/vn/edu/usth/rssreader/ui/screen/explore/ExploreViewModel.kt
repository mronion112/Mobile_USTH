package vn.edu.usth.rssreader.ui.screen.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.usth.rssreader.RssReaderApplication
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus

class ExploreViewModel(application: Application) : AndroidViewModel(application) {

    private val articleRepository = (application as RssReaderApplication).articleRepository
    private val sourceRepository = (application as RssReaderApplication).rssSourceRepository

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var activeSourcesCache: List<RssSource> = emptyList()

    init {
        observeArticles()
        observeBookmarks()
        observeSources()
    }

    // ─── observers ──────────────────────────────────────────

    private fun observeArticles() {
        viewModelScope.launch {
            articleRepository.articles.collect { articles ->
                _uiState.update { it.copy(allArticles = articles) }
                recompute()
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            articleRepository.bookmarkedIds.collect { ids ->
                _uiState.update { it.copy(bookmarkedIds = ids) }
            }
        }
    }

    private fun observeSources() {
        viewModelScope.launch {
            sourceRepository.sources.collect { sources ->
                activeSourcesCache = sources
                    ?.filter { it.status != RssSourceStatus.DISABLED }
                    .orEmpty()
                recompute()
            }
        }
    }

    // ─── actions ────────────────────────────────────────────

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(searchQuery = q, visibleCount = ExploreUiState.PAGE_SIZE) }
        recompute()
    }

    fun onCategorySelected(id: String) {
        _uiState.update { it.copy(selectedCategoryId = id, visibleCount = ExploreUiState.PAGE_SIZE) }
        recompute()
    }

    fun onLoadMore() {
        _uiState.update {
            it.copy(visibleCount = it.visibleCount + ExploreUiState.PAGE_SIZE)
        }
        recomputeVisible()
    }

    fun onToggleBookmark(article: Article, bookmarked: Boolean) {
        viewModelScope.launch {
            val current = articleRepository.bookmarkedIds.value.contains(article.id)
            if (current != bookmarked) {
                articleRepository.toggleBookmark(article)
            }
        }
    }

    // ─── derive ─────────────────────────────────────────────

    private fun recompute() {
        val state = _uiState.value
        val activeIds = activeSourcesCache.map { it.id }.toSet()

        val activeCategories = activeSourcesCache.map { it.category }.distinctBy { it.id }
        val displayCategories = listOf(FakeCategories.All) + activeCategories

        val validSelected = if (displayCategories.any { it.id == state.selectedCategoryId }) {
            state.selectedCategoryId
        } else {
            FakeCategories.All.id
        }

        val sourceValid = state.allArticles.filter {
            it.sourceId == -1L || it.sourceId in activeIds
        }

        val categoryFiltered = if (validSelected == FakeCategories.All.id) {
            sourceValid
        } else {
            sourceValid.filter { it.category.id == validSelected }
        }

        val query = state.searchQuery.trim()
        val queryFiltered = if (query.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.filter { it.matchesQuery(query) }
        }

        val sorted = queryFiltered.sortedByDescending { it.publishedAt }

        val trending = categoryFiltered
            .groupBy { it.sourceId }
            .values
            .mapNotNull { perSource -> perSource.maxByOrNull { it.publishedAt } }
            .sortedByDescending { it.publishedAt }
            .take(ExploreUiState.TRENDING_SIZE)

        val visible = sorted.take(state.visibleCount)

        _uiState.update {
            it.copy(
                displayCategories = displayCategories,
                selectedCategoryId = validSelected,
                filteredArticles = sorted,
                visibleArticles = visible,
                trendingArticles = trending,
                hasAnySource = activeSourcesCache.isNotEmpty(),
                isLoading = state.allArticles.isEmpty() && activeSourcesCache.isNotEmpty()
            )
        }
    }

    private fun recomputeVisible() {
        _uiState.update {
            it.copy(visibleArticles = it.filteredArticles.take(it.visibleCount))
        }
    }

    private fun Article.matchesQuery(q: String): Boolean {
        val lower = q.lowercase()
        return title.lowercase().contains(lower) ||
                description?.lowercase()?.contains(lower) == true ||
                source.lowercase().contains(lower) ||
                category.name.lowercase().contains(lower) ||
                author?.lowercase()?.contains(lower) == true
    }
}