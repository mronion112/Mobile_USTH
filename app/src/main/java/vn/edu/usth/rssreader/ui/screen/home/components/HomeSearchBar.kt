package vn.edu.usth.rssreader.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextMuted

/**
 * Fake search bar: chỉ hiển thị placeholder + icon. Bấm vào bất kỳ đâu
 * (kể cả icon Tune) → gọi [onClick]. Home không nhập liệu tại chỗ;
 * navigate sang tab Explore để search thực sự.
 */
@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "Tìm kiếm dòng sự kiện, từ khóa RSS...",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCardSubtle)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = placeholder,
            modifier = Modifier.weight(1f),
            color = TextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            imageVector = Icons.Filled.Tune,
            contentDescription = "Bộ lọc",
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FF)
@Composable
private fun PreviewHomeSearchBar() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.padding(16.dp)
    ) {
        HomeSearchBar(onClick = {})
    }
}