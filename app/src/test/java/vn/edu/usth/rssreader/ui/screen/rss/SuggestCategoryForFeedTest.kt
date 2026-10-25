package vn.edu.usth.rssreader.ui.screen.rss

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import vn.edu.usth.rssreader.data.model.Category

class SuggestCategoryForFeedTest {

    private val categories = listOf(
        Category("the-gioi", "Thế giới", 0),
        Category("cong-nghe", "Công nghệ", 2),
        Category("thoi-su", "Thời sự", 5)
    )

    @Test
    fun `url containing a category id selects that category`() {
        val result = suggestCategoryForFeed(
            url = "https://dantri.com.vn/rss/the-gioi.rss",
            title = "Dân trí",
            categories = categories
        )

        assertEquals("the-gioi", result?.id)
    }

    @Test
    fun `title containing a category name selects that category`() {
        val result = suggestCategoryForFeed(
            url = "https://example.com/rss/pickles.xml",
            title = "Tin tức Công nghệ mới",
            categories = categories
        )

        assertEquals("cong-nghe", result?.id)
    }

    @Test
    fun `unknown url and title select nothing`() {
        val result = suggestCategoryForFeed(
            url = "https://example.com/rss/pickles.xml",
            title = "Something entirely different",
            categories = categories
        )

        assertNull(result)
    }
}
