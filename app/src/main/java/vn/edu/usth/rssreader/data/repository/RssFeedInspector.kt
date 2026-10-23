package vn.edu.usth.rssreader.data.repository

import android.util.Xml
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

sealed interface RssFeedValidationResult {
    data class Valid(
        val detectedTitle: String?,
        val detectedSiteName: String?
    ) : RssFeedValidationResult
    data class Invalid(val message: String) : RssFeedValidationResult
}

/** Small on-device RSS/Atom check used by S06 refresh and the add-source form. */
class RssFeedInspector {
    suspend fun validate(rssUrl: String): RssFeedValidationResult = withContext(Dispatchers.IO) {
        val url = rssUrl.trim()
        if (!url.isHttpUrl()) {
            return@withContext RssFeedValidationResult.Invalid("RSS URL không hợp lệ")
        }

        var connection: HttpURLConnection? = null
        try {
            connection = (URL(url).openConnection() as? HttpURLConnection)
                ?: return@withContext RssFeedValidationResult.Invalid("RSS URL không hợp lệ")
            connection.instanceFollowRedirects = true
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml")

            if (connection.responseCode !in 200..299) {
                return@withContext RssFeedValidationResult.Invalid("Không thể kết nối nguồn RSS")
            }
            connection.inputStream.use { input ->
                readFeedTitle(input, connection.url.host.takeIf(String::isNotBlank))
            }
        } catch (_: Exception) {
            RssFeedValidationResult.Invalid("RSS URL không hợp lệ hoặc không thể truy cập")
        } finally {
            connection?.disconnect()
        }
    }

    private fun readFeedTitle(
        input: java.io.InputStream,
        siteName: String?
    ): RssFeedValidationResult {
        val parser = Xml.newPullParser().apply {
            runCatching { setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false) }
            setInput(input, null)
        }
        var rootSeen = false
        var title: String? = null

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val tag = parser.name.lowercase()
                if (!rootSeen) {
                    rootSeen = true
                    if (tag !in SUPPORTED_ROOT_TAGS) {
                        return RssFeedValidationResult.Invalid("Nội dung không phải RSS hoặc Atom hợp lệ")
                    }
                } else if (tag == "title" && title == null) {
                    title = parser.nextText().trim().takeIf(String::isNotBlank)?.take(MAX_TITLE_LENGTH)
                }
            }
            parser.next()
        }

        return if (rootSeen) {
            RssFeedValidationResult.Valid(title, siteName)
        } else {
            RssFeedValidationResult.Invalid("Nội dung RSS trống hoặc không hợp lệ")
        }
    }

    private fun String.isHttpUrl(): Boolean =
        startsWith("https://", ignoreCase = true) || startsWith("http://", ignoreCase = true)

    private companion object {
        const val CONNECT_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 15_000
        const val MAX_TITLE_LENGTH = 120
        val SUPPORTED_ROOT_TAGS = setOf("rss", "feed", "rdf")
    }
}
