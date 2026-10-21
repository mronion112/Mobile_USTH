package vn.edu.usth.rssreader.core.model

data class Category(
    val id: Long,
    val name: String,
    val slug: String,
)

enum class RssSourceStatus {
    ACTIVE,
    FETCHING,
    ERROR,
    DISABLED,
    ;

    companion object {
        fun fromStorage(value: String): RssSourceStatus = entries.firstOrNull {
            it.name.equals(value.trim(), ignoreCase = true)
        } ?: ERROR
    }
}

data class RssSource(
    val id: Long,
    val name: String,
    val rssUrl: String,
    val category: Category,
    val status: RssSourceStatus,
    val lastFetchedAt: String?,
    val lastError: String?,
    val createdAt: String,
)

data class FetchError(
    val code: String,
    val message: String,
)
