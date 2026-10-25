package vn.edu.usth.rssreader.ui.screen.saved

import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category

enum class SavedSortMode {
    SAVED_NEWEST,   // Lưu gần nhất (savedAt desc) — default
    SAVED_OLDEST    // Lưu cũ nhất (savedAt asc)
}

data class SavedUiState(
    val bookmarkedArticles: List<Article> = emptyList(),
    val displayCategories: List<Category> = emptyList(),
    val selectedCategoryId: String = "all",
    val sortMode: SavedSortMode = SavedSortMode.SAVED_NEWEST
)