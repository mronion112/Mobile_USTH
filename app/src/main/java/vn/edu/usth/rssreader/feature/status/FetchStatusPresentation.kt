package vn.edu.usth.rssreader.feature.status

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.core.model.RssSourceStatus

data class SourceFetchStatusUi(
    val label: String,
    val lastFetchedLabel: String,
    val errorMessage: String?,
    val isFetching: Boolean,
    val canRetry: Boolean,
    val isVisible: Boolean,
)

fun RssSource.toFetchStatusUi(
    now: Instant = Instant.now(),
    zoneId: ZoneId = ZoneId.systemDefault(),
): SourceFetchStatusUi {
    val lastFetchedLabel = formatRelativeFetchTime(lastFetchedAt, now, zoneId)

    return when (status) {
        RssSourceStatus.ACTIVE -> SourceFetchStatusUi(
            label = "Hoạt động",
            lastFetchedLabel = lastFetchedLabel,
            errorMessage = null,
            isFetching = false,
            canRetry = false,
            isVisible = true,
        )

        RssSourceStatus.FETCHING -> SourceFetchStatusUi(
            label = "Đang cập nhật",
            lastFetchedLabel = lastFetchedLabel,
            errorMessage = null,
            isFetching = true,
            canRetry = false,
            isVisible = true,
        )

        RssSourceStatus.ERROR -> SourceFetchStatusUi(
            label = "Cập nhật thất bại",
            lastFetchedLabel = lastFetchedLabel,
            errorMessage = safeErrorSummary(lastError),
            isFetching = false,
            canRetry = true,
            isVisible = true,
        )

        RssSourceStatus.DISABLED -> SourceFetchStatusUi(
            label = "Đã tắt",
            lastFetchedLabel = lastFetchedLabel,
            errorMessage = null,
            isFetching = false,
            canRetry = false,
            isVisible = false,
        )
    }
}

fun fetchErrorMessage(error: FetchError): String = when (error.code) {
    "RSS_FEED_INVALID" -> "Nguồn RSS không hợp lệ. Vui lòng kiểm tra lại địa chỉ nguồn."
    "RSS_FETCH_FAILED" -> "Chưa thể cập nhật nguồn tin. Vui lòng thử lại."
    "DUPLICATE_SOURCE" -> "Nguồn RSS này đã được thêm trước đó."
    "RESOURCE_NOT_FOUND" -> "Không tìm thấy nguồn RSS này."
    else -> safeErrorSummary(error.message)
}

internal fun safeErrorSummary(rawMessage: String?): String {
    val fallback = "Chưa thể cập nhật nguồn tin. Vui lòng thử lại."
    val normalized = rawMessage?.trim().orEmpty()
    if (normalized.isBlank()) return fallback

    val containsTechnicalDetail = TECHNICAL_DETAIL_PATTERNS.any { pattern ->
        pattern.containsMatchIn(normalized)
    }
    if (containsTechnicalDetail) return fallback

    return normalized.take(MAX_ERROR_LENGTH)
}

internal fun formatRelativeFetchTime(
    timestamp: String?,
    now: Instant,
    zoneId: ZoneId,
): String {
    if (timestamp.isNullOrBlank()) return "Chưa cập nhật lần nào"

    val fetchedAt = runCatching { Instant.parse(timestamp) }.getOrNull()
        ?: return "Chưa cập nhật lần nào"
    val elapsed = Duration.between(fetchedAt, now).coerceAtLeast(Duration.ZERO)

    return when {
        elapsed.toMinutes() < 1 -> "Vừa cập nhật"
        elapsed.toHours() < 1 -> "Cập nhật ${elapsed.toMinutes()} phút trước"
        elapsed.toDays() < 1 -> "Cập nhật ${elapsed.toHours()} giờ trước"
        elapsed.toDays() < 7 -> "Cập nhật ${elapsed.toDays()} ngày trước"
        else -> {
            val formattedDate = DATE_FORMATTER.withZone(zoneId).format(fetchedAt)
            "Cập nhật ngày $formattedDate"
        }
    }
}

private const val MAX_ERROR_LENGTH = 160
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val TECHNICAL_DETAIL_PATTERNS = listOf(
    Regex("https?://", RegexOption.IGNORE_CASE),
    Regex("<[^>]+>"),
    Regex("exception|stack\\s*trace", RegexOption.IGNORE_CASE),
    Regex("\\bat\\s+[\\w.$]+\\(", RegexOption.IGNORE_CASE),
    Regex("token|password|credential|secret|authorization|bearer", RegexOption.IGNORE_CASE),
)
