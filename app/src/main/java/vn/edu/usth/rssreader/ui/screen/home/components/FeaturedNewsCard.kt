package vn.edu.usth.rssreader.ui.screen.home.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import vn.edu.usth.rssreader.ui.components.article.CategoryBadgeVariant
import vn.edu.usth.rssreader.ui.theme.OnPrimary
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.PrimaryFixed
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import vn.edu.usth.rssreader.util.formatCompact
import vn.edu.usth.rssreader.util.formatRelativeTime

@Composable
fun FeaturedNewsCard(
    article: Article,
    modifier: Modifier = Modifier,
    bookmarked: Boolean = false,
    onBookmarkToggle: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    onReadNowClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
    ) {
        // ─── Image area (208dp) ─────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(208.dp)
                .background(SurfaceCardSubtle)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient bottom (transparent → dark 70%)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                TextPrimary.copy(alpha = 0f),
                                TextPrimary.copy(alpha = 0f),
                                TextPrimary.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Top-left: badges
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryBadge(
                    category = article.category,
                    variant = CategoryBadgeVariant.OnImage
                )
                OverlayTag(text = "Tiêu điểm 24h")
            }

            // Top-right: bookmark
            BookmarkButton(
                bookmarked = bookmarked,
                onToggle = onBookmarkToggle,
                variant = BookmarkButtonVariant.Overlay,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )

            // Bottom row: time + view count
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = OnPrimary.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = formatRelativeTime(article.publishedAt),
                        color = OnPrimary.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                article.viewCount?.let { views ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(TextPrimary.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = OnPrimary.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${formatCompact(views)} lượt xem",
                            color = OnPrimary.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // ─── Content area ───────────────────────────────
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Source • Author
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = article.source,
                    color = Primary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (!article.author.isNullOrBlank()) {
                    Text("•", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = article.author,
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Title
            Text(
                text = article.title,
                color = TextPrimary,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.27).sp
            )

            // Description (max 2 lines)
            article.description?.let {
                Text(
                    text = it,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            // Footer: RSS Verified + "Đọc ngay"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (article.isRssVerified) {
                    RssVerifiedBadge()
                } else {
                    Spacer(Modifier.size(1.dp))
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onReadNowClick)
                        .padding(
                            top = 6.dp,
                            end = 6.dp,
                            bottom = 4.dp,
                            start = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Đọc ngay",
                        color = Primary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.24.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OverlayTag(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(TextPrimary.copy(alpha = 0.7f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = OnPrimary,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.33.sp
    )
}

@Composable
private fun RssVerifiedBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PrimaryFixed.copy(alpha = 0.3f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.RssFeed,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "RSS Verified",
            color = Primary,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.33.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewFeaturedNewsCard() {
    var saved by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(16.dp)) {
        FeaturedNewsCard(
            article = FakeArticles.featured,
            bookmarked = saved,
            onBookmarkToggle = { saved = it }
        )
    }
}