package vn.edu.usth.rssreader.ui.screen.detail

import vn.edu.usth.rssreader.data.model.Article

sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState

    data class Success(
        val article: Article,
        val parsedData: ParsedArticleData,
        val isBookmarked: Boolean,
        val isFallback: Boolean = false
    ) : ArticleDetailUiState

    data class Error(
        val message: String
    ) : ArticleDetailUiState
}
