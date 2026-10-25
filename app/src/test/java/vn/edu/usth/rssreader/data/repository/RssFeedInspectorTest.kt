package vn.edu.usth.rssreader.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RssFeedInspectorTest {

    private val inspector = RssFeedInspector()

    @Test
    fun `garbage input is rejected without network access`() = runTest {
        val result = inspector.validate("not a url")

        assertTrue(result is RssFeedValidationResult.Invalid)
        assertEquals("RSS URL không hợp lệ", (result as RssFeedValidationResult.Invalid).message)
    }

    @Test
    fun `non-http scheme is rejected without network access`() = runTest {
        val result = inspector.validate("ftp://example.com/feed.rss")

        assertTrue(result is RssFeedValidationResult.Invalid)
    }

    @Test
    fun `blank input is rejected without network access`() = runTest {
        val result = inspector.validate("   ")

        assertTrue(result is RssFeedValidationResult.Invalid)
    }
}
