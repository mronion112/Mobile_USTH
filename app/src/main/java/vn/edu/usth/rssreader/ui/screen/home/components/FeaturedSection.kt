package vn.edu.usth.rssreader.ui.screen.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.components.TrailingLink
import vn.edu.usth.rssreader.ui.theme.SecondaryFixed
import vn.edu.usth.rssreader.ui.theme.StatusWarning
import vn.edu.usth.rssreader.ui.theme.TextPrimary

@Composable
fun FeaturedSection(
    article: Article?,
    modifier: Modifier = Modifier,
    bookmarked: Boolean = false,
    onBookmarkToggle: (Boolean) -> Unit = {},
    onCardClick: () -> Unit = {},
    onReadNowClick: () -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {
    if (article == null) return

    Column(modifier = modifier.fillMaxWidth()) {
        // ─── Header row ──────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Tin nổi bật hôm nay",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.27).sp
                )
                HotBadge()
            }

            TrailingLink(
                text = "Tất cả",
                onClick = onSeeAllClick,
                showChevron = true
            )
        }

        Spacer(Modifier.height(8.dp))

        // ─── Card ────────────────────────────────────
        FeaturedNewsCard(
            article = article,
            modifier = Modifier.padding(horizontal = 16.dp),
            bookmarked = bookmarked,
            onBookmarkToggle = onBookmarkToggle,
            onClick = onCardClick,
            onReadNowClick = onReadNowClick
        )
    }
}

@Composable
private fun HotBadge() {
    val transition = rememberInfiniteTransition(label = "hotPulse")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hotAlpha"
    )

    Text(
        text = "🔥 Nóng",
        modifier = Modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(50))
            .background(SecondaryFixed)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        color = StatusWarning,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.33.sp
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewFeaturedSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        FeaturedSection(article = FakeArticles.featured)
    }
}