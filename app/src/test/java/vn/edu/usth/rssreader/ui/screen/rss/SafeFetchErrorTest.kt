package vn.edu.usth.rssreader.ui.screen.rss

import org.junit.Assert.assertEquals
import org.junit.Test

class SafeFetchErrorTest {

    @Test
    fun `uses a fallback for blank errors`() {
        assertEquals(
            "Chưa thể cập nhật nguồn tin. Vui lòng thử lại.",
            safeFetchErrorMessage("  ")
        )
    }

    @Test
    fun `redacts technical details and URLs`() {
        assertEquals(
            "Chưa thể cập nhật nguồn tin. Vui lòng thử lại.",
            safeFetchErrorMessage("java.net.SocketException at feed.Parser.parse(Feed.kt:42) https://secret.test")
        )
    }

    @Test
    fun `preserves short user-facing errors`() {
        assertEquals(
            "Không thể kết nối nguồn RSS",
            safeFetchErrorMessage(" Không thể kết nối nguồn RSS ")
        )
    }

    @Test
    fun `truncates oversized user-facing errors`() {
        assertEquals(160, safeFetchErrorMessage("a".repeat(200)).length)
    }
}
