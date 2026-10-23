package vn.edu.usth.rssreader.ui.screen.explore.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.fake.FakeExploreTrending
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.components.TrailingLink
import vn.edu.usth.rssreader.ui.theme.Secondary
import vn.edu.usth.rssreader.ui.theme.TextPrimary

@Composable
fun TrendingTopicsSection(
    articles: List<Article>,
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {},
    onArticleClick: (String) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ─── Header ──────────────────────────────
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
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Chủ đề thịnh hành",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.27).sp
                )
            }

            TrailingLink(
                text = "Xem tất cả",
                onClick = onSeeAllClick,
                showChevron = false
            )
        }

        Spacer(Modifier.height(8.dp))

        // ─── Carousel ────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = articles, key = { it.id }) { article ->
                TrendingTopicCard(
                    article = article,
                    onClick = { onArticleClick(article.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewTrendingTopicsSection() {
    Box(modifier = Modifier.padding(vertical = 16.dp)) {
        TrendingTopicsSection(articles = FakeExploreTrending.list)
    }
}