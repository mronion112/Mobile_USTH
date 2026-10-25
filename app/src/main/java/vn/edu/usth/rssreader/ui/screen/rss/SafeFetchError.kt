package vn.edu.usth.rssreader.ui.screen.rss

private const val FALLBACK_FETCH_ERROR = "Chưa thể cập nhật nguồn tin. Vui lòng thử lại."
private const val MAX_FETCH_ERROR_LENGTH = 160

private val technicalDetailPatterns = listOf(
    Regex("https?://", RegexOption.IGNORE_CASE),
    Regex("<[^>]+>"),
    Regex("exception|stack\\s*trace", RegexOption.IGNORE_CASE),
    Regex("\\bat\\s+[\\w.$]+\\(", RegexOption.IGNORE_CASE),
    Regex("token|password|credential|secret|authorization|bearer", RegexOption.IGNORE_CASE)
)

internal fun safeFetchErrorMessage(rawMessage: String?): String {
    val normalized = rawMessage?.trim().orEmpty()
    if (normalized.isBlank()) return FALLBACK_FETCH_ERROR

    if (technicalDetailPatterns.any { it.containsMatchIn(normalized) }) {
        return FALLBACK_FETCH_ERROR
    }

    return normalized.take(MAX_FETCH_ERROR_LENGTH)
}
