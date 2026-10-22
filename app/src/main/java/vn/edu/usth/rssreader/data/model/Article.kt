package vn.edu.usth.rssreader.data.model

import java.time.Instant

data class Article(
    val id: String,
    val title: String,
    val description: String? = null,
    val imageUrl: String,
    val category: Category,
    val source: String,
    val author: String? = null,
    val publishedAt: Instant,
    val viewCount: Int? = null,
    val isRssVerified: Boolean = false,
    val readingTimeMinutes: Int? = null
)