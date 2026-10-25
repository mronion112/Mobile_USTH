package vn.edu.usth.rssreader.ui.screen.explore

import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category

data class ExploreUiState(
    val searchQuery: String = "",
    val selectedCategoryId: String = FakeCategories.All.id,

    // Data từ repo
    val allArticles: List<Article> = emptyList(),
    val displayCategories: List<Category> = emptyList(),

    // Sau filter
    val filteredArticles: List<Article> = emptyList(),
    val visibleArticles: List<Article> = emptyList(),
    val visibleCount: Int = PAGE_SIZE,

    val trendingArticles: List<Article> = emptyList(),

    val bookmarkedIds: Set<String> = emptySet(),

    val hasAnySource: Boolean = false,
    val isLoading: Boolean = true
) {
    val remainingCount: Int
        get() = (filteredArticles.size - visibleArticles.size).coerceAtLeast(0)

    val hasMore: Boolean
        get() = remainingCount > 0

    companion object {
        const val PAGE_SIZE = 10
        const val TRENDING_SIZE = 5
    }
}