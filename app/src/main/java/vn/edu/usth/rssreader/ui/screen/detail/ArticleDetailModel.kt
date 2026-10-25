package vn.edu.usth.rssreader.ui.screen.detail

sealed interface ArticleContentBlock {
    data class Paragraph(
        val text: String,
        val isFirst: Boolean = false
    ) : ArticleContentBlock

    data class Heading(
        val text: String,
        val level: Int = 2
    ) : ArticleContentBlock

    data class InlineImage(
        val url: String,
        val caption: String?
    ) : ArticleContentBlock

    data class Quote(
        val text: String
    ) : ArticleContentBlock
}

data class ParsedArticleData(
    val title: String,
    val sapo: String?,
    val author: String?,
    val publishedTime: String?,
    val heroImageUrl: String?,
    val heroCaption: String?,
    val blocks: List<ArticleContentBlock>,
    val originalUrl: String
)
