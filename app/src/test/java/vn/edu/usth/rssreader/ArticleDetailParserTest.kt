package vn.edu.usth.rssreader

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.ui.screen.detail.ArticleContentBlock
import vn.edu.usth.rssreader.ui.screen.detail.ArticleHtmlParser
import java.time.Instant

class ArticleDetailParserTest {

    private val testCategory = Category(id = "tech", name = "Công nghệ", colorIndex = 2)

    private val sampleArticle = Article(
        id = "test-1",
        title = "Tiêu đề bài báo thử nghiệm",
        description = "Mô tả ngắn gọn từ RSS feed phục vụ kiểm thử.",
        link = "",
        imageUrl = "https://example.com/test.jpg",
        category = testCategory,
        source = "Dân trí",
        author = "Phóng viên AI",
        publishedAt = Instant.now()
    )

    @Test
    fun testFallbackWhenLinkIsBlank() = runTest {
        val result = ArticleHtmlParser.parseArticle(sampleArticle)

        assertEquals("Tiêu đề bài báo thử nghiệm", result.title)
        assertEquals("Mô tả ngắn gọn từ RSS feed phục vụ kiểm thử.", result.sapo)
        assertEquals("Phóng viên AI", result.author)
        assertEquals(1, result.blocks.size)

        val firstBlock = result.blocks.first()
        assertTrue(firstBlock is ArticleContentBlock.Paragraph)
        assertEquals("Mô tả ngắn gọn từ RSS feed phục vụ kiểm thử.", (firstBlock as ArticleContentBlock.Paragraph).text)
        assertTrue(firstBlock.isFirst)
    }

    @Test
    fun testFallbackFromArticleMethod() {
        val result = ArticleHtmlParser.fallbackFromArticle(sampleArticle)

        assertEquals("Tiêu đề bài báo thử nghiệm", result.title)
        assertEquals("https://example.com/test.jpg", result.heroImageUrl)
        assertNotNull(result.blocks)
        assertEquals(1, result.blocks.size)
    }
}
