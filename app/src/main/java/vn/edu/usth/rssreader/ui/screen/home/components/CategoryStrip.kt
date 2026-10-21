package vn.edu.usth.rssreader.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.ui.components.article.CategoryPill
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun CategoryStrip(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    syncedFeedCount: Int = 14
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
            Text(
                text = "CHUYÊN MỤC DÂN TRÍ",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )
            Text(
                text = "Đang đồng bộ $syncedFeedCount luồng",
                color = Primary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        // ─── Pills row ───────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = categories, key = { it.id }) { category ->
                CategoryPill(
                    category = category,
                    selected = category.id == selectedCategoryId,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FF)
@Composable
private fun PreviewCategoryStrip() {
    CategoryStrip(
        categories = FakeCategories.strip,
        selectedCategoryId = FakeCategories.Business.id,
        onCategorySelected = {}
    )
}