package vn.edu.usth.rssreader.ui.screen.rss

import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.data.model.RssSource

data class RssSourcesUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val sources: List<RssSource> = emptyList(),
    val addSource: AddSourceUiState? = null,
    val pendingRemoval: PendingSourceRemoval? = null,
    val removingSourceId: Long? = null,
    val removalError: String? = null
)

data class AddSourceUiState(
    val name: String,
    val rssUrl: String,
    val category: Category,
    val categories: List<Category>,
    val isValidating: Boolean = false,
    val isSaving: Boolean = false,
    val validatedRssUrl: String? = null,
    val detectedTitle: String? = null,
    val detectedSiteName: String? = null,
    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() = name.isNotBlank() && validatedRssUrl == rssUrl.trim() && !isValidating && !isSaving
}

data class PendingSourceRemoval(
    val id: Long,
    val name: String
)
