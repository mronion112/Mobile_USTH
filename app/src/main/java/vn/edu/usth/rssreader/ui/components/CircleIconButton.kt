package vn.edu.usth.rssreader.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.edu.usth.rssreader.ui.theme.StatusWarning
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    backgroundColor: Color = SurfaceCard,
    iconTint: Color = TextSecondary,
    spinning: Boolean = false,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(spinning) {
        if (spinning) {
            rotation.animateTo(
                targetValue = rotation.value + 360f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
            )
            rotation.snapTo(0f)
        }
    }

    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer { rotationZ = rotation.value }
            )
        }
        overlay()
    }
}

// ─── Previews ──────────────────────────────────────────────────

@Preview(name = "Sync (spinning)", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewSync() {
    Box(modifier = Modifier.padding(16.dp)) {
        CircleIconButton(
            icon = Icons.Filled.Sync,
            contentDescription = "Đồng bộ RSS",
            onClick = {},
            spinning = true
        )
    }
}

@Preview(name = "Notification + ping badge", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewNotifWithBadge() {
    Box(modifier = Modifier.padding(16.dp)) {
        CircleIconButton(
            icon = Icons.Filled.Notifications,
            contentDescription = "Thông báo",
            onClick = {},
            overlay = {
                StatusDot(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp),
                    type = StatusDotType.Ping,
                    size = 8.dp,
                    color = StatusWarning
                )
            }
        )
    }
}

@Preview(name = "Search 44dp", showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
private fun PreviewSearch() {
    Box(modifier = Modifier.padding(16.dp)) {
        CircleIconButton(
            icon = Icons.Filled.Search,
            contentDescription = "Tìm kiếm",
            onClick = {},
            size = 44.dp,
            iconSize = 22.dp
        )
    }
}