package vn.edu.usth.rssreader.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.ui.theme.ActionTeal
import vn.edu.usth.rssreader.ui.theme.BorderSubtle
import vn.edu.usth.rssreader.ui.theme.Canvas
import vn.edu.usth.rssreader.ui.theme.MobileUSTHTheme
import vn.edu.usth.rssreader.ui.theme.MutedSlate
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import vn.edu.usth.rssreader.ui.theme.White
import vn.edu.usth.rssreader.ui.state.ScreenState

@Composable
fun <T> ScreenStateContent(
    state: ScreenState<T>,
    onRetry: () -> Unit,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    onEmptyAction: (() -> Unit)? = null,
) {
    when (state) {
        ScreenState.Loading -> LoadingState(modifier = modifier)
        is ScreenState.Empty -> EmptyState(
            title = state.title,
            message = state.message,
            modifier = modifier,
            actionLabel = state.actionLabel,
            onAction = onEmptyAction,
        )
        is ScreenState.Error -> ErrorState(
            message = state.message,
            onRetry = onRetry,
            modifier = modifier,
            retryLabel = state.retryLabel,
        )
        is ScreenState.Content -> content(state.data)
    }
}

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    itemCount: Int = 3,
) {
    val transition = rememberInfiniteTransition(label = "loading-skeleton")
    val alpha = transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "loading-skeleton-alpha",
    ).value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(16.dp)
            .semantics { contentDescription = "Đang tải nội dung" },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(itemCount.coerceAtLeast(1)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(16.dp),
                    alpha = alpha,
                )
                SkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    alpha = alpha,
                )
                SkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(12.dp),
                    alpha = alpha,
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    StateMessage(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Inbox,
                contentDescription = null,
                tint = MutedSlate,
                modifier = Modifier.size(48.dp),
            )
        },
        title = title,
        message = message,
        actionLabel = actionLabel,
        onAction = onAction,
    )
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retryLabel: String = "Thử lại",
) {
    StateMessage(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = StatusError,
                modifier = Modifier.size(48.dp),
            )
        },
        title = "Chưa thể tải nội dung",
        message = message,
        actionLabel = retryLabel,
        onAction = onRetry,
    )
}

@Composable
private fun StateMessage(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onAction,
                modifier = Modifier.heightIn(min = 44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ActionTeal,
                    contentColor = White,
                ),
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    alpha: Float,
) {
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .clip(RoundedCornerShape(8.dp))
            .background(BorderSubtle),
    )
}

@Preview(name = "S09 Empty", widthDp = 390, heightDp = 700)
@Composable
private fun EmptyStatePreview() {
    MobileUSTHTheme {
        EmptyState(
            title = "Chưa có nguồn RSS",
            message = "Thêm nguồn RSS đầu tiên để bắt đầu nhận tin mới.",
            actionLabel = "Thêm nguồn",
            onAction = {},
        )
    }
}

@Preview(name = "S09 Error", widthDp = 390, heightDp = 700)
@Composable
private fun ErrorStatePreview() {
    MobileUSTHTheme {
        ErrorState(
            message = "Chưa thể cập nhật nguồn tin. Vui lòng thử lại.",
            onRetry = {},
        )
    }
}

@Preview(name = "S09 Loading", widthDp = 390, heightDp = 700)
@Composable
private fun LoadingStatePreview() {
    MobileUSTHTheme {
        LoadingState()
    }
}
