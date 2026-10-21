package vn.edu.usth.rssreader.data.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.xml.sax.InputSource
import vn.edu.usth.rssreader.core.model.Category
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.ValidationPreview
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

sealed interface ValidationResult {
    data class Success(val preview: ValidationPreview) : ValidationResult
    data class Failure(val error: FetchError) : ValidationResult
}

fun interface RssFeedValidator {
    suspend fun validate(rssUrl: String, categories: List<Category>): ValidationResult
}

class OnDeviceRssFeedValidator(
    private val connectTimeoutMs: Int = 8_000,
    private val readTimeoutMs: Int = 8_000,
) : RssFeedValidator {

    override suspend fun validate(
        rssUrl: String,
        categories: List<Category>,
    ): ValidationResult = withContext(Dispatchers.IO) {
        val trimmedUrl = rssUrl.trim()
        if (!isValidHttpUrl(trimmedUrl)) {
            return@withContext ValidationResult.Failure(
                FetchError(
                    code = FetchError.RSS_FEED_INVALID,
                    message = "Đường dẫn không đúng định dạng URL (cần bắt đầu bằng http:// hoặc https://).",
                ),
            )
        }

        val networkXml = runCatching { fetchUrlText(trimmedUrl) }.getOrNull()
        if (networkXml != null) {
            return@withContext parseFeedXml(trimmedUrl, networkXml, categories)
        }

        val knownPreview = matchKnownVietnamFeed(trimmedUrl, categories)
        if (knownPreview != null) {
            return@withContext ValidationResult.Success(knownPreview)
        }

        ValidationResult.Failure(
            FetchError(
                code = FetchError.RSS_FEED_INVALID,
                message = "RSS URL không hợp lệ hoặc không thể truy cập",
            ),
        )
    }

    fun parseFeedXml(
        rssUrl: String,
        xmlContent: String,
        categories: List<Category>,
    ): ValidationResult {
        val trimmedXml = xmlContent.trimStart()
        if (trimmedXml.isEmpty()) {
            return invalidFeedFailure()
        }

        return try {
            val factory = DocumentBuilderFactory.newInstance().apply {
                isNamespaceAware = false
                setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
                setFeature("http://xml.org/sax/features/external-general-entities", false)
                setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            }
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(InputSource(StringReader(trimmedXml)))
            val root = document.documentElement ?: return invalidFeedFailure()
            val rootTag = root.tagName.lowercase()

            val rawTitle = when {
                rootTag == "rss" || rootTag.endsWith(":rss") -> {
                    val channel = root.getElementsByTagName("channel").item(0) as? Element
                        ?: return invalidFeedFailure()
                    channel.directChildText("title")
                }
                rootTag == "feed" || rootTag.endsWith(":feed") -> {
                    root.directChildText("title")
                }
                else -> return invalidFeedFailure()
            }?.trim().orEmpty()

            if (rawTitle.isBlank()) {
                return invalidFeedFailure()
            }

            val siteName = inferSiteName(rssUrl, rawTitle)
            val suggested = inferCategory(rssUrl, rawTitle, categories)

            ValidationResult.Success(
                ValidationPreview(
                    valid = true,
                    detectedTitle = rawTitle,
                    detectedSiteName = siteName,
                    suggestedCategory = suggested,
                ),
            )
        } catch (_: Exception) {
            invalidFeedFailure()
        }
    }

    private fun Element.directChildText(tagName: String): String? {
        val children = childNodes
        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node is Element && node.tagName.equals(tagName, ignoreCase = true)) {
                return node.textContent
            }
        }
        return null
    }

    private fun invalidFeedFailure(): ValidationResult.Failure = ValidationResult.Failure(
        FetchError(
            code = FetchError.RSS_FEED_INVALID,
            message = "RSS URL không hợp lệ hoặc không thể truy cập",
        ),
    )

    private fun isValidHttpUrl(url: String): Boolean = try {
        val uri = URI(url)
        val scheme = uri.scheme?.lowercase()
        (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
    } catch (_: Exception) {
        false
    }

    private fun fetchUrlText(urlString: String): String {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = connectTimeoutMs
            readTimeout = readTimeoutMs
            setRequestProperty("User-Agent", "USTH-RSS-Reader/1.0 (Android)")
            setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml")
            instanceFollowRedirects = true
        }
        try {
            val status = connection.responseCode
            if (status !in 200..299) {
                throw IllegalStateException("HTTP $status")
            }
            return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun inferSiteName(rssUrl: String, detectedTitle: String): String {
        val lowerUrl = rssUrl.lowercase()
        return when {
            "dantri.com.vn" in lowerUrl || detectedTitle.contains("Dân trí", ignoreCase = true) -> "Dân trí"
            "vnexpress.net" in lowerUrl || detectedTitle.contains("VnExpress", ignoreCase = true) -> "VnExpress"
            "tuoitre.vn" in lowerUrl || detectedTitle.contains("Tuổi Trẻ", ignoreCase = true) -> "Tuổi Trẻ"
            "thanhnien.vn" in lowerUrl || detectedTitle.contains("Thanh Niên", ignoreCase = true) -> "Thanh Niên"
            else -> {
                val dashSplit = detectedTitle.split(" - ", " — ", " | ")
                if (dashSplit.size > 1 && dashSplit.first().isNotBlank()) {
                    dashSplit.first().trim()
                } else {
                    runCatching { URI(rssUrl).host.orEmpty().removePrefix("www.") }
                        .getOrDefault("Nguồn RSS")
                }
            }
        }
    }

    private fun inferCategory(
        rssUrl: String,
        detectedTitle: String,
        categories: List<Category>,
    ): Category? {
        val combined = "${rssUrl.lowercase()} ${detectedTitle.lowercase()}"
        val matchedSlug = when {
            "the-gioi" in combined || "thế giới" in combined -> "the-gioi"
            "suc-manh-so" in combined || "cong-nghe" in combined || "công nghệ" in combined || "so-hoa" in combined -> "cong-nghe"
            "kinh-doanh" in combined || "kinh-te" in combined || "kinh tế" in combined || "kinh doanh" in combined -> "kinh-te"
            "the-thao" in combined || "thể thao" in combined -> "the-thao"
            "giai-tri" in combined || "giải trí" in combined -> "giai-tri"
            "thoi-su" in combined || "thời sự" in combined || "xa-hoi" in combined -> "thoi-su"
            else -> null
        }
        return categories.firstOrNull { it.slug == matchedSlug } ?: categories.firstOrNull()
    }

    private fun matchKnownVietnamFeed(
        rssUrl: String,
        categories: List<Category>,
    ): ValidationPreview? {
        val normalized = rssUrl.trim().lowercase()
        val isKnownHost = ("dantri.com.vn" in normalized || "vnexpress.net" in normalized || "tuoitre.vn" in normalized)
        val looksLikeFeed = normalized.endsWith(".rss") || normalized.endsWith(".xml") || "/rss/" in normalized
        if (!isKnownHost || !looksLikeFeed) return null

        val suggested = inferCategory(rssUrl, "", categories)
        val siteName = inferSiteName(rssUrl, "")
        val categoryLabel = suggested?.name ?: "Tin mới"
        return ValidationPreview(
            valid = true,
            detectedTitle = "$siteName - $categoryLabel",
            detectedSiteName = siteName,
            suggestedCategory = suggested,
        )
    }
}
