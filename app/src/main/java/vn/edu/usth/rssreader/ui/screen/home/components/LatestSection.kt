package vn.edu.usth.rssreader.ui.screen.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.components.StatusDot
import vn.edu.usth.rssreader.ui.components.StatusDotType
import vn.edu.usth.rssreader.ui.components.TrailingLink
import vn.edu.usth.rssreader.ui.theme.StatusSuccess
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun LatestSection(
    articles: List<Article>,
    savedIds: Set<String>,
    isRefreshing: Boolean,
    onRefreshClick: () -> Unit,
    onBookmarkToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onArticleClick: (String) -> Unit = {},
    onSeeMoreClick: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ─── Header row ──────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: title + ping dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tin mới nhất",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.27).sp
                )
                StatusDot(
                    type = StatusDotType.Ping,
                    size = 8.dp,
                    color = StatusSuccess
                )
            }

            // Right: Refresh chip + "Xem thêm"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RefreshChip(isRefreshing = isRefreshing, onClick = onRefreshClick)
                TrailingLink(
                    text = "Xem thêm",
                    onClick = onSeeMoreClick,
                    showChevron = false
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ─── Items ───────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            articles.forEach { article ->
                NewsListItem(
                    article = article,
                    bookmarked = savedIds.contains(article.id),
                    onBookmarkToggle = { onBookmarkToggle(article.id, it) },
                    onClick = { onArticleClick(article.id) }
                )
            }
        }
    }
}

@Composable
private fun RefreshChip(
    isRefreshing: Boolean,
    onClick: () -> Unit
) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            rotation.animateTo(
                targetValue = rotation.value + 360f,
                animationSpec = tween(800, easing = LinearEasing)
            )
            rotation.snapTo(0f)
        }
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier
                .size(15.dp)
                .graphicsLayer { rotationZ = rotation.value }
        )
        Text(
            text = "Làm mới",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380, heightDp = 500)
@Composable
private fun PreviewLatestSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        LatestSection(
            articles = FakeArticles.latest,
            savedIds = setOf("a3"),
            isRefreshing = false,
            onRefreshClick = {},
            onBookmarkToggle = { _, _ -> }
        )
    }
}