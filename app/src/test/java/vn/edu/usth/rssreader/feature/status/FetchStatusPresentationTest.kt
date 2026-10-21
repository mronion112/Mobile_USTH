package vn.edu.usth.rssreader.feature.status

import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import vn.edu.usth.rssreader.core.model.Category
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.core.model.RssSourceStatus

class FetchStatusPresentationTest {

    private val now = Instant.parse("2026-10-21T10:00:00Z")

    @Test
    fun `active source shows relative last fetched time`() {
        val ui = source(
            status = RssSourceStatus.ACTIVE,
            lastFetchedAt = "2026-10-21T09:45:00Z",
        ).toFetchStatusUi(now = now, zoneId = ZoneOffset.UTC)

        assertEquals("Hoạt động", ui.label)
        assertEquals("Cập nhật 15 phút trước", ui.lastFetchedLabel)
        assertFalse(ui.canRetry)
    }

    @Test
    fun `fetching source disables duplicate retry`() {
        val ui = source(status = RssSourceStatus.FETCHING).toFetchStatusUi(now = now)

        assertTrue(ui.isFetching)
        assertFalse(ui.canRetry)
    }

    @Test
    fun `error source exposes safe retry state`() {
        val ui = source(
            status = RssSourceStatus.ERROR,
            lastError = "Không thể kết nối nguồn RSS.",
        ).toFetchStatusUi(now = now)

        assertEquals("Không thể kết nối nguồn RSS.", ui.errorMessage)
        assertTrue(ui.canRetry)
    }

    @Test
    fun `technical error details are replaced with safe copy`() {
        val message = safeErrorSummary(
            "java.net.SocketException at feed.Parser.parse(Feed.kt:42) https://secret.test",
        )

        assertEquals("Chưa thể cập nhật nguồn tin. Vui lòng thử lại.", message)
    }

    @Test
    fun `disabled source is hidden and cannot retry`() {
        val ui = source(status = RssSourceStatus.DISABLED).toFetchStatusUi(now = now)

        assertFalse(ui.isVisible)
        assertFalse(ui.canRetry)
    }

    @Test
    fun `known fetch error code maps to Vietnamese copy`() {
        val message = fetchErrorMessage(
            FetchError(code = "RSS_FEED_INVALID", message = "raw parser output"),
        )

        assertEquals(
            "Nguồn RSS không hợp lệ. Vui lòng kiểm tra lại địa chỉ nguồn.",
            message,
        )
    }

    @Test
    fun `stored status is parsed case insensitively with safe fallback`() {
        assertEquals(RssSourceStatus.ACTIVE, RssSourceStatus.fromStorage("active"))
        assertEquals(RssSourceStatus.ERROR, RssSourceStatus.fromStorage("unknown"))
    }

    private fun source(
        status: RssSourceStatus,
        lastFetchedAt: String? = null,
        lastError: String? = null,
    ) = RssSource(
        id = 3,
        name = "Dân trí — Công nghệ",
        rssUrl = "https://dantri.com.vn/rss/suc-manh-so.rss",
        category = Category(id = 9, name = "Công nghệ", slug = "cong-nghe"),
        status = status,
        lastFetchedAt = lastFetchedAt,
        lastError = lastError,
        createdAt = "2026-10-01T08:00:00Z",
    )
}
