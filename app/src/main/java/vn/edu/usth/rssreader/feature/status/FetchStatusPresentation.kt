package vn.edu.usth.rssreader.feature.status

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus
import vn.edu.usth.rssreader.ui.screen.rss.safeFetchErrorMessage

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
            errorMessage = safeFetchErrorMessage(lastError),
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

internal fun formatRelativeFetchTime(
    fetchedAt: Instant?,
    now: Instant,
    zoneId: ZoneId,
): String {
    if (fetchedAt == null) return "Chưa cập nhật lần nào"

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

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
