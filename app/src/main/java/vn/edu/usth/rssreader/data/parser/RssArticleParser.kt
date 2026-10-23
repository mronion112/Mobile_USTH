package vn.edu.usth.rssreader.data.parser

import android.util.Xml
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.RssSource
import java.io.InputStream
import java.security.MessageDigest
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class RssArticleParser {

    fun parse(input: InputStream, source: RssSource): Result<List<Article>> = runCatching {
        val parser = Xml.newPullParser().apply {
            runCatching { setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false) }
            setInput(input, null)
        }

        val articles = mutableListOf<Article>()
        var current: RawItem? = null

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.localName()) {
                        "item", "entry" -> current = RawItem()
                        else -> current?.consume(parser)
                    }
                }

                XmlPullParser.END_TAG -> {
                    when (parser.localName()) {
                        "item", "entry" -> {
                            current?.toArticle(source)?.let(articles::add)
                            current = null
                        }
                    }
                }
            }
            parser.next()
        }

        articles
    }

// ─── helpers ────────────────────────────────────────────

    private fun XmlPullParser.localName(): String =
        (name ?: "").substringAfterLast(':').lowercase(Locale.ROOT)

    private fun XmlPullParser.readTextSafely(): String {
        val sb = StringBuilder()
        var depth = 1
        try {
            while (depth > 0) {
                when (next()) {
                    XmlPullParser.START_TAG -> depth++
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.TEXT, XmlPullParser.CDSECT ->
                        if (depth >= 1) sb.append(text ?: "")

                    XmlPullParser.END_DOCUMENT -> return sb.toString()
                }
            }
        } catch (_: Exception) { /* ignore */
        }
        return sb.toString()
    }

    private fun RawItem.consume(parser: XmlPullParser) {
        when (parser.localName()) {
            "title" -> if (title == null) title = parser.readTextSafely()
            "link" -> if (link == null) {
                val href = parser.getAttributeValue(null, "href") // Atom
                link = if (!href.isNullOrBlank()) href else parser.readTextSafely()
            }

            "guid" -> if (guid == null) guid = parser.readTextSafely()
            "pubdate" -> if (pubDate == null) pubDate = parser.readTextSafely()
            "description", "summary" ->
                if (descriptionHtml == null) descriptionHtml = parser.readTextSafely()

            "enclosure" -> if (enclosureUrl == null) {
                val type = parser.getAttributeValue(null, "type")
                val url = parser.getAttributeValue(null, "url")
                if (!url.isNullOrBlank() && (type == null || type.startsWith("image"))) {
                    enclosureUrl = url
                }
            }

            "content" -> {
// Chỉ chấp nhận media:content (kiểm tra namespace để không bắt nhầm content:encoded)
                val ns = parser.namespace.orEmpty()
                if (ns.contains("media", ignoreCase = true) && mediaUrl == null) {
                    mediaUrl = parser.getAttributeValue(null, "url")?.takeIf { it.isNotBlank() }
                }
            }

            "creator", "author" -> if (author == null) author = parser.readTextSafely()
        }
    }

    private data class RawItem(
        var title: String? = null,
        var link: String? = null,
        var guid: String? = null,
        var pubDate: String? = null,
        var descriptionHtml: String? = null,
        var author: String? = null,
        var enclosureUrl: String? = null,
        var mediaUrl: String? = null
    ) {
        fun toArticle(source: RssSource): Article? {
            val cleanTitle = stripHtml(title) ?: return null
            val identity = (guid ?: link)?.takeIf { it.isNotBlank() } ?: return null

            val image = enclosureUrl
                ?: mediaUrl
                ?: extractFirstImage(descriptionHtml)
                ?: "" // theo yêu cầu: không set ảnh rác

            return Article(
                id = md5Hex(identity),
                sourceId = source.id,
                title = cleanTitle,
                description = stripHtml(descriptionHtml),
                imageUrl = image,
                category = source.category,
                source = source.name,
                author = stripHtml(author)?.takeIf { it.isNotBlank() },
                publishedAt = parseDate(pubDate) ?: Instant.now(),
                viewCount = null,
                isRssVerified = true,
                readingTimeMinutes = null
            )
        }
    }

// ─── static helpers ─────────────────────────────────────

    private companion object {
        fun stripHtml(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            return runCatching { Jsoup.parse(raw).text().trim() }
                .getOrNull()
                ?.takeIf { it.isNotBlank() }
        }

        fun extractFirstImage(html: String?): String? {
            if (html.isNullOrBlank()) return null
            return runCatching {
                Jsoup.parse(html).selectFirst("img[src]")?.attr("src")
                    ?.takeIf { it.isNotBlank() }
            }.getOrNull()
        }

        fun parseDate(raw: String?): Instant? {
            if (raw.isNullOrBlank()) return null
            val text = raw.trim()
            runCatching {
                return ZonedDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()
            }
            runCatching {
                return ZonedDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME).toInstant()
            }
            runCatching {
                return OffsetDateTime.parse(text).toInstant()
            }
            return null
        }

        fun md5Hex(value: String): String {
            val digest = MessageDigest.getInstance("MD5").digest(value.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}