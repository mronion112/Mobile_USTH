package vn.edu.usth.rssreader.ui.components.article

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.scheme

enum class CategoryBadgeVariant {
    Solid,
    OnImage
}

@Composable
fun CategoryBadge(
    category: Category,
    modifier: Modifier = Modifier,
    variant: CategoryBadgeVariant = CategoryBadgeVariant.Solid
) {
    val scheme = remember(category.id) { category.scheme() }

    val bg = when (variant) {
        CategoryBadgeVariant.Solid -> scheme.badgeBg
        CategoryBadgeVariant.OnImage -> SurfaceCard.copy(alpha = 0.9f)
    }
    val fg = when (variant) {
        CategoryBadgeVariant.Solid -> scheme.badgeContent
        CategoryBadgeVariant.OnImage -> Primary
    }
    val hPad = if (variant == CategoryBadgeVariant.OnImage) 10.dp else 8.dp
    val vPad = if (variant == CategoryBadgeVariant.OnImage) 4.dp else 2.dp

    Text(
        text = category.name,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = hPad, vertical = vPad),
        color = fg,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.33.sp  // label-sm: 0.03em × 11sp
    )
}

// ─── Previews ──────────────────────────────────────────────────

@Preview(name = "Solid (list items)", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewBadgeSolid() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryBadge(FakeCategories.Tech)
        CategoryBadge(FakeCategories.Business)
        CategoryBadge(FakeCategories.Sports)
        CategoryBadge(FakeCategories.Entertainment)
        CategoryBadge(FakeCategories.Current)
    }
}

@Preview(name = "OnImage (hero overlay)", showBackground = true, backgroundColor = 0xFF1E293B)
@Composable
private fun PreviewBadgeOnImage() {
    Box(modifier = Modifier.padding(16.dp)) {
        CategoryBadge(
            category = FakeCategories.World,
            variant = CategoryBadgeVariant.OnImage
        )
    }
}