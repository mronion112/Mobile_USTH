package vn.edu.usth.rssreader.ui.components.article

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.ui.theme.OnPrimary
import vn.edu.usth.rssreader.ui.theme.TagActiveBg
import vn.edu.usth.rssreader.ui.theme.TagInactiveBg
import vn.edu.usth.rssreader.ui.theme.TagInactiveText
import vn.edu.usth.rssreader.ui.theme.icon

@Composable
fun CategoryPill(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) TagActiveBg else TagInactiveBg
    val fg = if (selected) OnPrimary else TagInactiveText

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = category.icon(),
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = category.name,
            color = fg,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.24.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewCategoryPill() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryPill(FakeCategories.All, selected = false, onClick = {})
        CategoryPill(FakeCategories.World, selected = false, onClick = {})
        CategoryPill(FakeCategories.Business, selected = true, onClick = {})
        CategoryPill(FakeCategories.Tech, selected = false, onClick = {})
    }
}