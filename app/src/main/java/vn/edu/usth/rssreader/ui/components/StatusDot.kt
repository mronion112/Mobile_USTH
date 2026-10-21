package vn.edu.usth.rssreader.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.edu.usth.rssreader.ui.theme.PrimaryContainer
import vn.edu.usth.rssreader.ui.theme.StatusSuccess
import vn.edu.usth.rssreader.ui.theme.StatusWarning
import vn.edu.usth.rssreader.ui.theme.SurfaceCard

enum class StatusDotType { Pulse, Ping, Static }

@Composable
fun StatusDot(
    modifier: Modifier = Modifier,
    type: StatusDotType = StatusDotType.Static,
    size: Dp = 8.dp,
    color: Color = StatusSuccess,
    ringColor: Color? = null,
    ringWidth: Dp = 2.dp
) {
    when (type) {
        StatusDotType.Static -> {
            var m = modifier.size(size).clip(CircleShape).background(color)
            if (ringColor != null) {
                m = m.border(ringWidth, ringColor, CircleShape)
            }
            Box(m)
        }

        StatusDotType.Pulse -> {
            val transition = rememberInfiniteTransition(label = "pulse")
            val alpha by transition.animateFloat(
                initialValue = 1f,
                targetValue = 0.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )
            Box(
                modifier.size(size).clip(CircleShape).background(color.copy(alpha = alpha))
            )
        }

        StatusDotType.Ping -> {
            val transition = rememberInfiniteTransition(label = "ping")
            val scale by transition.animateFloat(
                initialValue = 1f,
                targetValue = 2.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "pingScale"
            )
            val alpha by transition.animateFloat(
                initialValue = 0.6f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "pingAlpha"
            )
            Box(modifier.size(size)) {
                Box(
                    Modifier
                        .size(size)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .clip(CircleShape)
                        .background(color)
                )
                // Solid layer
                Box(Modifier.size(size).clip(CircleShape).background(color))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewStatusDot() {
    Column(modifier = Modifier.padding(20.dp)) {
        Box(modifier = Modifier.padding(12.dp)) {
            StatusDot(type = StatusDotType.Pulse, size = 6.dp, color = PrimaryContainer)
        }
        Box(modifier = Modifier.padding(12.dp)) {
            StatusDot(type = StatusDotType.Ping, size = 8.dp, color = StatusWarning)
        }
        Box(modifier = Modifier.padding(12.dp)) {
            StatusDot(
                type = StatusDotType.Static,
                size = 12.dp,
                color = StatusSuccess,
                ringColor = SurfaceCard
            )
        }
        Box(modifier = Modifier.padding(12.dp)) {
            StatusDot(type = StatusDotType.Static, size = 8.dp, color = StatusSuccess)
        }
    }
}