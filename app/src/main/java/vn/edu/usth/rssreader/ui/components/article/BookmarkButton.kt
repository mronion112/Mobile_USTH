package vn.edu.usth.rssreader.ui.components.article

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextSecondary

enum class BookmarkButtonVariant {
    Overlay,
    Inline
}

@Composable
fun BookmarkButton(
    bookmarked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: BookmarkButtonVariant = BookmarkButtonVariant.Inline
) {
    val scale = remember { Animatable(1f) }
    var previous by remember { mutableStateOf(bookmarked) }
    LaunchedEffect(bookmarked) {
        if (previous != bookmarked) {
            scale.animateTo(1.25f, animationSpec = tween(120))
            scale.animateTo(1f, animationSpec = tween(180))
        }
        previous = bookmarked
    }

    val icon = if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder
    val tint = when (variant) {
        BookmarkButtonVariant.Overlay -> if (bookmarked) Primary else TextSecondary
        BookmarkButtonVariant.Inline  -> if (bookmarked) Primary else TextMuted
    }
    val containerSize: Dp = when (variant) {
        BookmarkButtonVariant.Overlay -> 36.dp
        BookmarkButtonVariant.Inline  -> 32.dp
    }
    val iconSize: Dp = when (variant) {
        BookmarkButtonVariant.Overlay -> 20.dp
        BookmarkButtonVariant.Inline  -> 18.dp
    }
    val containerBg = when (variant) {
        BookmarkButtonVariant.Overlay -> SurfaceCard.copy(alpha = 0.9f)
        BookmarkButtonVariant.Inline  -> Color.Transparent
    }

    Box(
        modifier = modifier
            .size(containerSize)
            .clip(CircleShape)
            .background(containerBg)
            .clickable { onToggle(!bookmarked) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (bookmarked) "Bỏ lưu" else "Lưu bài viết",
            tint = tint,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewBookmarkInline() {
    var saved by remember { mutableStateOf(false) }
    Box(modifier = Modifier) {
        BookmarkButton(
            bookmarked = saved,
            onToggle = { saved = it },
            variant = BookmarkButtonVariant.Inline
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E293B)
@Composable
private fun PreviewBookmarkOverlay() {
    var saved by remember { mutableStateOf(true) }
    Box(modifier = Modifier) {
        BookmarkButton(
            bookmarked = saved,
            onToggle = { saved = it },
            variant = BookmarkButtonVariant.Overlay
        )
    }
}