package vn.edu.usth.rssreader.ui.screen.saved

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
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus

class SavedViewModel(application: Application) : AndroidViewModel(application) {

    private val articleRepository = (application as RssReaderApplication).articleRepository
    private val sourceRepository = (application as RssReaderApplication).rssSourceRepository

    private val _uiState = MutableStateFlow(
        SavedUiState(selectedCategoryId = FakeCategories.All.id)
    )
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    private var activeSourcesCache: List<RssSource> = emptyList()

    init {
        viewModelScope.launch {
            sourceRepository.sources.collect { sources ->
                activeSourcesCache = sources
                    ?.filter { it.status != RssSourceStatus.DISABLED }
                    .orEmpty()
                recompute()
            }
        }
        viewModelScope.launch {
            articleRepository.bookmarkedArticles.collect { articles ->
                _uiState.update { it.copy(bookmarkedArticles = articles) }
                recompute()
            }
        }
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onSortModeChange(mode: SavedSortMode) {
        _uiState.update { it.copy(sortMode = mode) }
    }

    fun onUnsave(article: Article) {
        viewModelScope.launch {
            articleRepository.toggleBookmark(article)
        }
    }
    private fun recompute() {
        val current = _uiState.value

        val sourceCategories = activeSourcesCache
            .map { it.category }
            .distinctBy { it.id }

        val sourceIds = sourceCategories.map { it.id }.toSet()

        val bookmarkOnlyCategories = current.bookmarkedArticles
            .map { it.category }
            .distinctBy { it.id }
            .filterNot { it.id in sourceIds }

        val displayCategories: List<Category> =
            listOf(FakeCategories.All) + sourceCategories + bookmarkOnlyCategories

        // Nếu category đang chọn không còn trong danh sách → reset về "Tất cả"
        val validSelected = if (displayCategories.any { it.id == current.selectedCategoryId }) {
            current.selectedCategoryId
        } else {
            FakeCategories.All.id
        }

        _uiState.update {
            it.copy(
                displayCategories = displayCategories,
                selectedCategoryId = validSelected
            )
        }
    }
}