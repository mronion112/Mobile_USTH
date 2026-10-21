package vn.edu.usth.rssreader.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.data.parser.OnDeviceRssFeedValidator
import vn.edu.usth.rssreader.data.parser.ValidationResult
import vn.edu.usth.rssreader.data.repository.RssSourceRepository

class RssFeedValidatorTest {

    private val validator = OnDeviceRssFeedValidator()
    private val categories = RssSourceRepository.defaultCategories()

    @Test
    fun `parseFeedXml extracts title, siteName and suggested category from valid RSS 2_0 XML`() {
        val sampleXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <rss version="2.0">
              <channel>
                <title>Dân trí - Thế giới</title>
                <link>https://dantri.com.vn/the-gioi.htm</link>
                <description>Tin tức thế giới</description>
                <item>
                  <title>Bài viết mẫu</title>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val result = validator.parseFeedXml(
            rssUrl = "https://dantri.com.vn/rss/the-gioi.rss",
            xmlContent = sampleXml,
            categories = categories,
        )

        assertTrue(result is ValidationResult.Success)
        val preview = (result as ValidationResult.Success).preview
        assertTrue(preview.valid)
        assertEquals("Dân trí - Thế giới", preview.detectedTitle)
        assertEquals("Dân trí", preview.detectedSiteName)
        assertNotNull(preview.suggestedCategory)
        assertEquals("the-gioi", preview.suggestedCategory?.slug)
    }

    @Test
    fun `parseFeedXml returns RSS_FEED_INVALID when XML is not an RSS or Atom feed`() {
        val htmlContent = "<html><body>Not a feed</body></html>"
        val result = validator.parseFeedXml(
            rssUrl = "https://example.com/not-rss",
            xmlContent = htmlContent,
            categories = categories,
        )

        assertTrue(result is ValidationResult.Failure)
        val error = (result as ValidationResult.Failure).error
        assertEquals(FetchError.RSS_FEED_INVALID, error.code)
    }

    @Test
    fun `repository rejects duplicate RSS URL with DUPLICATE_SOURCE`() = runTest {
        val repo = RssSourceRepository(validator)
        val result = repo.validateRssUrl("https://dantri.com.vn/rss/thoi-su.rss")

        assertTrue(result is ValidationResult.Failure)
        val error = (result as ValidationResult.Failure).error
        assertEquals(FetchError.DUPLICATE_SOURCE, error.code)
    }
}
