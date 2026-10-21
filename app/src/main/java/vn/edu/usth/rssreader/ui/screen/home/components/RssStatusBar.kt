package vn.edu.usth.rssreader.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.ui.components.StatusDot
import vn.edu.usth.rssreader.ui.components.StatusDotType
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.PrimaryFixed
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.StatusSuccess
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun RssStatusBar(
    modifier: Modifier = Modifier,
    feedName: String = "Luồng tin Dân trí RSS 2.0",
    refreshCycle: String = "Chu kỳ nạp: Mỗi 10 phút",
    online: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), clip = false)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ─── Left: icon + text stack ─────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryFixed.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.RssFeed,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = feedName,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.33.sp
                )
                Text(
                    text = refreshCycle,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // ─── Right: status indicator ────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusDot(
                type = StatusDotType.Static,
                size = 8.dp,
                color = if (online) StatusSuccess else StatusError
            )
            Text(
                text = if (online) "Trực tuyến" else "Ngoại tuyến",
                color = if (online) StatusSuccess else StatusError,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.33.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewRssStatusBar() {
    Column(modifier = Modifier.padding(16.dp)) {
        RssStatusBar()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380)
@Composable
private fun PreviewRssStatusBarOffline() {
    Column(modifier = Modifier.padding(16.dp)) {
        RssStatusBar(online = false)
    }
}