package vn.edu.usth.rssreader.data.model

import java.time.Instant

/**
 * A locally subscribed RSS feed.  Articles deliberately do not belong to this
 * model: disabling a source only stops future fetches and never deletes the
 * articles or bookmarks already saved on the device.
 */
data class RssSource(
    val id: Long,
    val name: String,
    val rssUrl: String,
    val category: Category,
    val status: RssSourceStatus,
    val lastFetchedAt: Instant?,
    val lastError: String?,
    val createdAt: Instant
)

enum class RssSourceStatus {
    ACTIVE,
    FETCHING,
    ERROR,
    DISABLED
}
