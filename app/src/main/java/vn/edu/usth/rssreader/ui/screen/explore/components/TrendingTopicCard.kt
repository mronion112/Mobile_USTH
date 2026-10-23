package vn.edu.usth.rssreader.ui.screen.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import vn.edu.usth.rssreader.data.fake.FakeExploreTrending
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.theme.OnPrimary
import vn.edu.usth.rssreader.ui.theme.PrimaryContainer
import vn.edu.usth.rssreader.ui.theme.Secondary
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TertiaryContainer
import vn.edu.usth.rssreader.util.formatRelativeTime

private val TrendingBadgePalette: List<Color> = listOf(
    /* 0 - blue   */ TertiaryContainer,
    /* 1 - orange */ Secondary,
    /* 2 - teal   */ PrimaryContainer
)

@Composable
fun TrendingTopicCard(
    article: Article,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val badgeColor = TrendingBadgePalette[
        article.category.colorIndex.mod(TrendingBadgePalette.size)
    ]

    Box(
        modifier = modifier
            .width(270.dp)
            .height(175.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCardSubtle)
            .clickable(onClick = onClick)
    ) {
        // ─── Background Image ────────────────────────────
        AsyncImage(
            model = article.imageUrl,
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ─── Gradient overlay ──────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF020617).copy(alpha = 0.9f),
                            Color(0xFF0F172A).copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ─── Content bottom ────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Badge + time row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Badge — label lấy từ Category.name
                Text(
                    text = article.category.name,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeColor.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = OnPrimary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.33.sp
                )

                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = formatRelativeTime(article.publishedAt),
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Title
            Text(
                text = article.title,
                color = OnPrimary,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.16).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 320)
@Composable
private fun PreviewTrendingTopicCard() {
    Box(modifier = Modifier.padding(16.dp)) {
        TrendingTopicCard(article = FakeExploreTrending.list[0])
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 320)
@Composable
private fun PreviewTrendingTopicCardOrange() {
    Box(modifier = Modifier.padding(16.dp)) {
        TrendingTopicCard(article = FakeExploreTrending.list[1])
    }
}