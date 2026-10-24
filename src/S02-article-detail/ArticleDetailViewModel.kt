package vn.edu.usth.rssreader.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.fake.FakeExploreArticles
import vn.edu.usth.rssreader.data.fake.FakeExploreTrending
import vn.edu.usth.rssreader.data.model.Article

class ArticleDetailViewModel(
    private val articleId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        loadArticle()
    }

    fun loadArticle() {
        val article = findArticle(articleId)
        if (article == null) {
            _uiState.value = ArticleDetailUiState.Error("Không tìm thấy bài viết hoặc bài viết đã bị xóa.")
            return
        }

        _uiState.value = ArticleDetailUiState.Loading

        viewModelScope.launch {
            val isBookmarked = BookmarkStore.isBookmarked(article.id)
            val parsedData = ArticleHtmlParser.parseArticle(article)
            val isFallback = parsedData.blocks.isEmpty() ||
                    (article.link.isBlank() || !article.link.startsWith("http"))

            _uiState.update {
                ArticleDetailUiState.Success(
                    article = article,
                    parsedData = parsedData,
                    isBookmarked = isBookmarked,
                    isFallback = isFallback
                )
            }
        }
    }

    fun toggleBookmark() {
        val current = _uiState.value
        if (current is ArticleDetailUiState.Success) {
            val newStatus = BookmarkStore.toggle(current.article.id)
            _uiState.update {
                current.copy(isBookmarked = newStatus)
            }
        }
    }

    private fun findArticle(id: String): Article? {
        val allArticles = FakeArticles.all + FakeExploreArticles.results + FakeExploreTrending.list
        return allArticles.firstOrNull { it.id == id }
    }

    companion object {
        fun provideFactory(articleId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ArticleDetailViewModel(articleId) as T
                }
            }
    }
}
