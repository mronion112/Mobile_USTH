package vn.edu.usth.rssreader.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.usth.rssreader.RssReaderApplication
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Reader
import vn.edu.usth.rssreader.data.model.RssSourceStatus
import vn.edu.usth.rssreader.util.greetingForNow

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as RssReaderApplication
    private val articleRepository = app.articleRepository
    private val sourceRepository = app.rssSourceRepository

    private val _uiState = MutableStateFlow(
        HomeUiState(
            greeting = greetingForNow(),
            reader = Reader.DEFAULT,
            hasUnreadNotifications = true,
            categories = FakeCategories.strip,
            selectedCategoryId = FakeCategories.All.id
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    init {
        observeArticles()
        observeBookmarks()
        observeRefreshState()
        observeSources()
    }

    // ─── observers ──────────────────────────────────────────

    private fun observeArticles() {
        viewModelScope.launch {
            articleRepository.articles.collect { articles ->
                applyArticles(articles)
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            articleRepository.bookmarkedIds.collect { ids ->
                _uiState.update { it.copy(savedArticleIds = ids) }
            }
        }
    }

    private fun observeRefreshState() {
        viewModelScope.launch {
            articleRepository.isRefreshing.collect { refreshing ->
                _uiState.update {
                    it.copy(isRefreshing = refreshing, isSyncing = refreshing)
                }
            }
        }
        viewModelScope.launch {
            articleRepository.refreshError.collect { error ->
                _uiState.update { it.copy(refreshError = error) }
                applyArticles(articleRepository.articles.value)
            }
        }
    }

    private fun observeSources() {
        viewModelScope.launch {
            sourceRepository.sources.collect { sources ->
                val activeSources = sources
                    ?.filter { it.status != RssSourceStatus.DISABLED }
                    .orEmpty()

                val activeCategories = activeSources
                    .map { it.category }
                    .distinctBy { it.id }

                val displayCategories = listOf(FakeCategories.All) + activeCategories

                val currentSelected = _uiState.value.selectedCategoryId
                val validSelected = if (displayCategories.any { it.id == currentSelected }) {
                    currentSelected
                } else {
                    FakeCategories.All.id
                }

                _uiState.update {
                    it.copy(
                        categories = displayCategories,
                        selectedCategoryId = validSelected,
                        syncedFeedCount = activeSources.count { s -> s.status == RssSourceStatus.ACTIVE },
                        hasAnySource = activeSources.isNotEmpty()
                    )
                }
                applyArticles(articleRepository.articles.value)
            }
        }
    }

    // ─── derive ─────────────────────────────────────────────

    private fun applyArticles(all: List<Article>) {
        val activeIds = sourceRepository.sources.value
            ?.filter { it.status != RssSourceStatus.DISABLED }
            ?.map { it.id }
            ?.toSet()
            ?: emptySet()

        val visibleCategoryIds = _uiState.value.categories.map { it.id }.toSet()

        val valid = all.filter { article ->
            (article.sourceId == -1L || article.sourceId in activeIds) &&
                    article.category.id in visibleCategoryIds
        }

        _uiState.update { state ->
            val filtered = when {
                state.selectedCategoryId == null ||
                        state.selectedCategoryId == FakeCategories.All.id -> valid
                else -> valid.filter { it.category.id == state.selectedCategoryId }
            }
            state.copy(
                featuredArticle = filtered.firstOrNull(),
                latestArticles = filtered.drop(1).take(MAX_LATEST),
                isInitialLoading = valid.isEmpty() &&
                        state.hasAnySource &&
                        state.refreshError == null
            )
        }
    }

    // ─── actions ────────────────────────────────────────────
    fun onNotificationClick() {
        _uiState.update { it.copy(hasUnreadNotifications = false) }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        applyArticles(articleRepository.articles.value)
    }

    fun onToggleBookmark(article: Article, bookmarked: Boolean) {
        viewModelScope.launch {
            val current = articleRepository.bookmarkedIds.value.contains(article.id)
            if (current != bookmarked) {
                articleRepository.toggleBookmark(article)
            }
        }
    }

    fun onSyncClick() = refreshWithFeedback()

    fun onRefreshClick() = refreshWithFeedback()

    private fun refreshWithFeedback() {
        if (articleRepository.isRefreshing.value) return
        viewModelScope.launch {
            val before = articleRepository.articles.value.map { it.id }.toSet()
            articleRepository.refreshAll()
            val after = articleRepository.articles.value.map { it.id }.toSet()
            val newCount = (after - before).size
            val error = articleRepository.refreshError.value

            val message = when {
                newCount > 0 -> "Đã cập nhật $newCount bài mới"
                error != null -> error
                else -> "Không có bài mới"
            }
            _toastEvents.emit(message)
        }
    }

    private companion object {
        const val MAX_LATEST = 20
    }
}