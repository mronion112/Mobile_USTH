package vn.edu.usth.rssreader.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.components.article.BookmarkButton
import vn.edu.usth.rssreader.ui.components.article.BookmarkButtonVariant
import vn.edu.usth.rssreader.ui.components.article.CategoryBadge
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.util.formatRelativeTime

@Composable
fun NewsListItem(
    article: Article,
    bookmarked: Boolean,
    onBookmarkToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp), clip = false)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ─── Left: text column ──────────────────────
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row: category badge + time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryBadge(category = article.category)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = formatRelativeTime(article.publishedAt),
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Title — max 2 lines
            Text(
                text = article.title,
                color = TextPrimary,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.16).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Footer: source + bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.source,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                BookmarkButton(
                    bookmarked = bookmarked,
                    onToggle = onBookmarkToggle,
                    variant = BookmarkButtonVariant.Inline
                )
            }
        }

        // ─── Right: 96dp image ──────────────────────
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCardSubtle)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(96.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewNewsListItem() {
    Box(modifier = Modifier.padding(16.dp)) {
        NewsListItem(
            article = FakeArticles.latest[0],
            bookmarked = false,
            onBookmarkToggle = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewNewsListItemBookmarked() {
    Box(modifier = Modifier.padding(16.dp)) {
        NewsListItem(
            article = FakeArticles.latest[1],
            bookmarked = true,
            onBookmarkToggle = {}
        )
    }
}