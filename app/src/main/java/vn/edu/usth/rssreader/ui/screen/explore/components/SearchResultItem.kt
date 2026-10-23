package vn.edu.usth.rssreader.ui.screen.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import vn.edu.usth.rssreader.data.fake.FakeExploreArticles
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.components.HighlightedText
import vn.edu.usth.rssreader.ui.components.ShareIconButton
import vn.edu.usth.rssreader.ui.components.article.BookmarkButton
import vn.edu.usth.rssreader.ui.components.article.BookmarkButtonVariant
import vn.edu.usth.rssreader.ui.theme.PrimaryContainer
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import vn.edu.usth.rssreader.util.formatRelativeTime

@Composable
fun SearchResultItem(
    article: Article,
    keyword: String,
    bookmarked: Boolean,
    onBookmarkToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // ─── Text column ──────────────────────────
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Meta row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = article.source,
                    color = PrimaryContainer,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.33.sp
                )
                Dot()
                Text(
                    text = article.category.name,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Dot()
                Text(
                    text = formatRelativeTime(article.publishedAt),
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            // Title with highlight
            HighlightedText(
                text = article.title,
                keyword = keyword,
                maxLines = 2
            )

            val trimmedKeyword = keyword.trim()
            if (trimmedKeyword.isNotBlank()) {
                val titleMatches = article.title.contains(trimmedKeyword, ignoreCase = true)
                val descMatches = article.description?.contains(trimmedKeyword, ignoreCase = true) == true
                if (!titleMatches && descMatches) {
                    val snippet = buildSnippet(article.description.orEmpty(), trimmedKeyword)
                    if (snippet.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        SnippetText(text = snippet, keyword = trimmedKeyword, maxLines = 2)
                    }
                }
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    article.readingTimeMinutes?.let { minutes ->
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "$minutes phút đọc",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    BookmarkButton(
                        bookmarked = bookmarked,
                        onToggle = onBookmarkToggle,
                        variant = BookmarkButtonVariant.Inline
                    )
                    ShareIconButton(onClick = onShareClick)
                }
            }
        }

        // ─── Thumbnail 84dp ─────────────────────
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCardSubtle)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(84.dp)
            )
        }
    }
}

private fun buildSnippet(text: String, keyword: String, contextChars: Int = 60): String {
    if (keyword.isBlank() || text.isBlank()) return text.take(120)
    val idx = text.indexOf(keyword, ignoreCase = true)
    if (idx < 0) return text.take(120)
    val start = (idx - contextChars).coerceAtLeast(0)
    val end = (idx + keyword.length + contextChars).coerceAtMost(text.length)
    return buildString {
        if (start > 0) append("…")
        append(text.substring(start, end).trim())
        if (end < text.length) append("…")
    }
}

@Composable
private fun SnippetText(text: String, keyword: String, maxLines: Int) {
    val annotated = buildAnnotatedString {
        append(text)
        if (keyword.isNotBlank()) {
            var start = 0
            while (start < text.length) {
                val idx = text.indexOf(keyword, start, ignoreCase = true)
                if (idx < 0) break
                addStyle(
                    style = SpanStyle(
                        background = PrimaryContainer.copy(alpha = 0.18f),
                        fontWeight = FontWeight.SemiBold
                    ),
                    start = idx,
                    end = idx + keyword.length
                )
                start = idx + keyword.length
            }
        }
    }
    Text(
        text = annotated,
        color = TextSecondary,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Dot() {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(TextMuted)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewSearchResultItem() {
    Box(modifier = Modifier.padding(16.dp)) {
        SearchResultItem(
            article = FakeExploreArticles.results[0],
            keyword = "Trí tuệ nhân tạo",
            bookmarked = false,
            onBookmarkToggle = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewSearchResultItemBookmarked() {
    Box(modifier = Modifier.padding(16.dp)) {
        SearchResultItem(
            article = FakeExploreArticles.results[2],
            keyword = "Trí tuệ nhân tạo",
            bookmarked = true,
            onBookmarkToggle = {}
        )
    }
}