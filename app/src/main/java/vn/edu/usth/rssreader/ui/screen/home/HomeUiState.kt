package vn.edu.usth.rssreader.ui.screen.home

import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.data.model.Reader

data class HomeUiState(
    val isLoading: Boolean = false,

    // Greeting block
    val greeting: String = "",
    val reader: Reader? = null,
    val isSyncing: Boolean = false,
    val hasUnreadNotifications: Boolean = false,

    // Category strip
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,

    // Content
    val featuredArticle: Article? = null,
    val latestArticles: List<Article> = emptyList(),

    // Latest section state
    val isRefreshing: Boolean = false,
    val savedArticleIds: Set<String> = emptySet()
)