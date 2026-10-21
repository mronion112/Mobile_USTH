package vn.edu.usth.rssreader.core.model

data class Category(
    val id: Long,
    val name: String,
    val slug: String,
)

data class SourceRef(
    val id: Long,
    val name: String,
)

data class Article(
    val id: Long,
    val title: String,
    val description: String?,
    val link: String,
    val imageUrl: String?,
    val publishedAt: String?,
    val fetchedAt: String,
    val source: SourceRef,
    val category: Category,
    val isBookmarked: Boolean,
)

data class RssSource(
    val id: Long,
    val name: String,
    val rssUrl: String,
    val category: Category,
    val status: String,
    val lastFetchedAt: String?,
    val lastError: String?,
    val createdAt: String,
) {
    companion object {
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_FETCHING = "FETCHING"
        const val STATUS_ERROR = "ERROR"
    }
}

data class ValidationPreview(
    val valid: Boolean,
    val detectedTitle: String,
    val detectedSiteName: String,
    val suggestedCategory: Category?,
)

data class FetchError(
    val code: String,
    val message: String,
) {
    companion object {
        const val RSS_FEED_INVALID = "RSS_FEED_INVALID"
        const val RSS_FETCH_FAILED = "RSS_FETCH_FAILED"
        const val DUPLICATE_SOURCE = "DUPLICATE_SOURCE"
        const val RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"
        const val INTERNAL_ERROR = "INTERNAL_ERROR"
    }
}
