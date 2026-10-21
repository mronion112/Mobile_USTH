package vn.edu.usth.rssreader.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import vn.edu.usth.rssreader.data.model.Reader
import vn.edu.usth.rssreader.ui.components.CircleIconButton
import vn.edu.usth.rssreader.ui.components.StatusDot
import vn.edu.usth.rssreader.ui.components.StatusDotType
import vn.edu.usth.rssreader.ui.theme.PrimaryContainer
import vn.edu.usth.rssreader.ui.theme.StatusSuccess
import vn.edu.usth.rssreader.ui.theme.StatusWarning
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun GreetingRow(
    greeting: String,
    reader: Reader,
    isSyncing: Boolean,
    hasUnreadNotifications: Boolean,
    onSyncClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ─── Left: Avatar + Greeting ──────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AvatarWithOnlineDot(reader = reader, online = true)

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = greeting,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(Modifier.width(4.dp))
                    StatusDot(
                        type = StatusDotType.Pulse,
                        size = 6.dp,
                        color = PrimaryContainer
                    )
                }
                Text(
                    text = reader.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.16).sp
                )
            }
        }

        // ─── Right: Sync + Notification ──────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CircleIconButton(
                icon = Icons.Filled.Sync,
                contentDescription = "Đồng bộ RSS",
                onClick = onSyncClick,
                spinning = isSyncing
            )
            CircleIconButton(
                icon = Icons.Filled.Notifications,
                contentDescription = "Thông báo tin nóng",
                onClick = onNotificationClick,
                overlay = {
                    if (hasUnreadNotifications) {
                        StatusDot(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 6.dp, end = 6.dp),
                            type = StatusDotType.Ping,
                            size = 8.dp,
                            color = StatusWarning
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun AvatarWithOnlineDot(
    reader: Reader,
    online: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(44.dp)) {
        AsyncImage(
            model = reader.avatarUrl,
            contentDescription = "Avatar ${reader.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SurfaceCardSubtle)
        )
        if (online) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(14.dp)
                    .clip(shape = CircleShape)
                    .background(SurfaceCard),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(StatusSuccess)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FF)
@Composable
private fun PreviewGreetingRow() {
    Box(modifier = Modifier.padding(vertical = 16.dp)) {
        GreetingRow(
            greeting = "Xin chào buổi sáng",
            reader = Reader.DEFAULT,
            isSyncing = false,
            hasUnreadNotifications = true,
            onSyncClick = {},
            onNotificationClick = {}
        )
    }
}

@Preview(name = "Syncing state", showBackground = true, backgroundColor = 0xFFF8F9FF)
@Composable
private fun PreviewGreetingSyncing() {
    Box(modifier = Modifier.padding(vertical = 16.dp)) {
        GreetingRow(
            greeting = "Xin chào buổi tối",
            reader = Reader.DEFAULT,
            isSyncing = true,
            hasUnreadNotifications = false,
            onSyncClick = {},
            onNotificationClick = {}
        )
    }
}