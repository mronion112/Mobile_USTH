package vn.edu.usth.rssreader.ui.screen.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.usth.rssreader.RssReaderApplication
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.fake.FakeExploreArticles
import vn.edu.usth.rssreader.data.fake.FakeExploreTrending
import vn.edu.usth.rssreader.data.model.Article

class ArticleDetailViewModel(
    application: Application,
    private val articleId: String
) : AndroidViewModel(application) {

    private val articleRepository = (application as RssReaderApplication).articleRepository
    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        observeBookmarks()
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
            val parsedData = ArticleHtmlParser.parseArticle(article)
            _uiState.value = ArticleDetailUiState.Success(
                article = article,
                parsedData = parsedData,
                isBookmarked = article.id in articleRepository.bookmarkedIds.value,
                isFallback = parsedData.blocks.isEmpty() ||
                    article.link.isBlank() || !article.link.startsWith("http")
            )
        }
    }

    fun toggleBookmark() {
        val current = _uiState.value as? ArticleDetailUiState.Success ?: return
        viewModelScope.launch {
            val isBookmarked = articleRepository.toggleBookmark(current.article)
            _uiState.update { state ->
                if (state is ArticleDetailUiState.Success) {
                    state.copy(isBookmarked = isBookmarked)
                } else {
                    state
                }
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            articleRepository.bookmarkedIds.collect { ids ->
                _uiState.update { state ->
                    if (state is ArticleDetailUiState.Success) {
                        state.copy(isBookmarked = state.article.id in ids)
                    } else {
                        state
                    }
                }
            }
        }
    }

    private fun findArticle(id: String): Article? {
        val repositoryArticles = articleRepository.articles.value + articleRepository.bookmarkedArticles.value
        val allArticles = repositoryArticles + FakeArticles.all + FakeExploreArticles.results + FakeExploreTrending.list
        return allArticles.firstOrNull { it.id == id }
    }

    companion object {
        fun provideFactory(application: Application, articleId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (!modelClass.isAssignableFrom(ArticleDetailViewModel::class.java)) {
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                    }
                    return ArticleDetailViewModel(application, articleId) as T
                }
            }
    }
}
