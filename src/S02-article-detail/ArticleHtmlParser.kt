package vn.edu.usth.rssreader.ui.screen.detail

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import vn.edu.usth.rssreader.data.model.Article

object ArticleHtmlParser {

    private const val TAG = "ArticleHtmlParser"
    private const val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
    private const val TIMEOUT_MS = 10_000

    suspend fun parseArticle(article: Article): ParsedArticleData = withContext(Dispatchers.IO) {
        val link = article.link.trim()
        if (link.isBlank() || !link.startsWith("http")) {
            return@withContext fallbackFromArticle(article)
        }

        try {
            val doc: Document = Jsoup.connect(link)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get()

            parseDocument(doc, article)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse HTML for ${article.id} ($link): ${e.message}")
            fallbackFromArticle(article)
        }
    }

    private fun parseDocument(doc: Document, fallbackArticle: Article): ParsedArticleData {
        // 1. Tiêu đề
        val title = doc.selectFirst("h1[data-slot=\"title\"], h1.title-page, h1.dt-text-4xl, h1")
            ?.text()?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: fallbackArticle.title

        // 2. Sapo / Tóm tắt
        val sapo = doc.selectFirst("h2[data-slot=\"sapo\"], .singular-sapo, .article-sapo, [data-slot=\"sapo\"]")
            ?.text()?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: fallbackArticle.description

        // 3. Tác giả & Thời gian
        val author = doc.selectFirst(".author-name, .singular-author, [data-slot=\"author\"], meta[name=\"author\"]")
            ?.let { if (it.tagName() == "meta") it.attr("content") else it.text() }
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: fallbackArticle.author

        val publishedTime = doc.selectFirst(".author-time, .singular-time, time, meta[property=\"article:published_time\"]")
            ?.let { if (it.tagName() == "meta") it.attr("content") else it.text() }
            ?.trim()
            ?.takeIf { it.isNotBlank() }

        // 4. Hero image
        val heroImgEl = doc.selectFirst("figure img, article img")
        val heroUrl = extractImageUrl(heroImgEl) ?: fallbackArticle.imageUrl
        val heroCaption = doc.selectFirst("figure figcaption")?.text()?.trim()

        // 5. Khối nội dung
        val contentContainer = doc.selectFirst(
            "div[data-slot=\"content\"], article[data-slot=\"container\"], .singular-content, .article-content, div.detail__content, article:has(p)"
        )
        val blocks = mutableListOf<ArticleContentBlock>()

        if (contentContainer != null) {
            val contentElements = contentContainer.select("p, h2:not([data-slot=\"sapo\"]), h3, figure, blockquote")
            var isFirstParagraph = true

            for (el in contentElements) {
                // Bỏ qua nếu element nằm trong ads, related news hoặc copyright cuối bài
                if (isIgnoredElement(el)) continue

                when (el.tagName()) {
                    "p" -> {
                        val text = el.text().trim()
                        if (text.isNotBlank() && !isMetadataParagraph(text)) {
                            blocks.add(
                                ArticleContentBlock.Paragraph(
                                    text = text,
                                    isFirst = isFirstParagraph
                                )
                            )
                            isFirstParagraph = false
                        }
                    }
                    "h2", "h3" -> {
                        val text = el.text().trim()
                        if (text.isNotBlank()) {
                            blocks.add(
                                ArticleContentBlock.Heading(
                                    text = text,
                                    level = if (el.tagName() == "h2") 2 else 3
                                )
                            )
                        }
                    }
                    "figure" -> {
                        val img = el.selectFirst("img")
                        val url = extractImageUrl(img)
                        val caption = el.selectFirst("figcaption")?.text()?.trim()
                            ?: img?.attr("alt")?.trim()
                        if (!url.isNullOrBlank()) {
                            blocks.add(ArticleContentBlock.InlineImage(url = url, caption = caption))
                        }
                    }
                    "blockquote" -> {
                        val text = el.text().trim()
                        if (text.isNotBlank()) {
                            blocks.add(ArticleContentBlock.Quote(text = text))
                        }
                    }
                }
            }
        }

        val hasExtractedBlocks = blocks.isNotEmpty()

        // Nếu không trích xuất được paragraph nào, fallback về description
        if (!hasExtractedBlocks && !fallbackArticle.description.isNullOrBlank()) {
            blocks.add(ArticleContentBlock.Paragraph(fallbackArticle.description, isFirst = true))
        }

        return ParsedArticleData(
            title = if (hasExtractedBlocks) title else fallbackArticle.title,
            sapo = if (hasExtractedBlocks) sapo else fallbackArticle.description,
            author = if (hasExtractedBlocks) author else fallbackArticle.author,
            publishedTime = publishedTime,
            heroImageUrl = if (hasExtractedBlocks) heroUrl else fallbackArticle.imageUrl,
            heroCaption = heroCaption,
            blocks = blocks,
            originalUrl = fallbackArticle.link
        )
    }

    private fun extractImageUrl(img: Element?): String? {
        if (img == null) return null
        val dataOriginal = img.attr("data-original").trim()
        if (dataOriginal.isNotBlank() && !dataOriginal.startsWith("data:")) return dataOriginal

        val dataSrc = img.attr("data-src").trim()
        if (dataSrc.isNotBlank() && !dataSrc.startsWith("data:")) return dataSrc

        val src = img.attr("src").trim()
        if (src.isNotBlank() && !src.startsWith("data:")) return src

        return null
    }

    private fun isIgnoredElement(el: Element): Boolean {
        val parentClasses = el.parents().joinToString(" ") { it.className() + " " + it.id() }
        val ignoredKeywords = listOf("ads", "quang-cao", "banner", "relate", "comment", "social-share")
        return ignoredKeywords.any { parentClasses.contains(it, ignoreCase = true) }
    }

    private fun isMetadataParagraph(text: String): Boolean {
        val lower = text.lowercase()
        return lower.startsWith("theo dantri") ||
                lower.startsWith("ảnh:") ||
                lower.startsWith("video:") ||
                lower.startsWith("nguồn:")
    }

    fun fallbackFromArticle(article: Article): ParsedArticleData {
        val blocks = mutableListOf<ArticleContentBlock>()
        if (!article.description.isNullOrBlank()) {
            blocks.add(ArticleContentBlock.Paragraph(text = article.description, isFirst = true))
        }

        return ParsedArticleData(
            title = article.title,
            sapo = article.description,
            author = article.author,
            publishedTime = null,
            heroImageUrl = article.imageUrl,
            heroCaption = null,
            blocks = blocks,
            originalUrl = article.link
        )
    }
}
