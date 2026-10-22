package vn.edu.usth.rssreader.ui.screen.rss

import vn.edu.usth.rssreader.data.model.RssSource

data class RssSourcesUiState(
    val isLoading: Boolean = true,
    val sources: List<RssSource> = emptyList(),
    val pendingRemoval: PendingSourceRemoval? = null,
    val removingSourceId: Long? = null,
    val removalError: String? = null
)

data class PendingSourceRemoval(
    val id: Long,
    val name: String
)
