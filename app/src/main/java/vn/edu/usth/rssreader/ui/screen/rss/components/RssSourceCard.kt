package vn.edu.usth.rssreader.ui.screen.rss.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus
import vn.edu.usth.rssreader.ui.screen.rss.safeFetchErrorMessage
import vn.edu.usth.rssreader.ui.components.StatusDot
import vn.edu.usth.rssreader.ui.components.StatusDotType
import vn.edu.usth.rssreader.ui.theme.BorderSubtle
import vn.edu.usth.rssreader.ui.theme.PrimaryContainer
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.StatusSuccess
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import vn.edu.usth.rssreader.ui.theme.icon
import vn.edu.usth.rssreader.ui.theme.scheme
import vn.edu.usth.rssreader.util.formatRelativeTime

@Composable
fun RssSourceCard(
    source: RssSource,
    onRefresh: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFetching = source.status == RssSourceStatus.FETCHING
    val categoryColors = source.category.scheme()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.72f)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColors.badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = source.category.icon(),
                        contentDescription = null,
                        tint = categoryColors.badgeContent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = source.name,
                            modifier = Modifier.weight(1f, fill = false),
                            color = TextPrimary,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        CategoryBadge(source.category.name)
                    }
                    Text(
                        text = source.rssUrl,
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SourceStatus(source = source)
                Spacer(Modifier.width(8.dp))
                LastFetchedTime(source = source)
                Spacer(Modifier.weight(1f))
                if (source.status == RssSourceStatus.ERROR) {
                    TextButton(
                        onClick = { onRefresh(source.id) },
                        modifier = Modifier.height(44.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Thử lại",
                            color = PrimaryContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    IconButton(
                        onClick = { onRefresh(source.id) },
                        enabled = !isFetching,
                        modifier = Modifier.size(44.dp)
                    ) {
                        if (isFetching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = PrimaryContainer,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Làm mới ${source.name}",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                IconButton(
                    onClick = { onRemove(source.id) },
                    enabled = !isFetching,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Xóa ${source.name}",
                        tint = if (isFetching) TextMuted.copy(alpha = 0.5f) else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBadge(name: String) {
    Surface(
        color = SurfaceCardSubtle,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SourceStatus(source: RssSource) {
    when (source.status) {
        RssSourceStatus.ACTIVE -> {
            StatusPill(
                label = "Hoạt động",
                color = StatusSuccess,
                icon = {
                    StatusDot(
                        type = StatusDotType.Static,
                        size = 6.dp,
                        color = StatusSuccess
                    )
                }
            )
        }

        RssSourceStatus.FETCHING -> {
            StatusPill(
                label = "Đang nạp dữ liệu…",
                color = PrimaryContainer,
                icon = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = PrimaryContainer,
                        strokeWidth = 1.5.dp
                    )
                }
            )
        }

        RssSourceStatus.ERROR -> {
            StatusPill(
                label = safeFetchErrorMessage(source.lastError),
                color = StatusError,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.WarningAmber,
                        contentDescription = null,
                        tint = StatusError,
                        modifier = Modifier.size(13.dp)
                    )
                }
            )
        }

        RssSourceStatus.DISABLED -> Unit
    }
}

@Composable
private fun StatusPill(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: @Composable () -> Unit
) {
    Surface(
        color = color.copy(alpha = 0.09f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(
                text = label,
                color = color,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LastFetchedTime(source: RssSource) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Schedule,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = source.lastFetchedAt?.let(::formatRelativeTime) ?: "Chưa nạp",
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            maxLines = 1
        )
    }
}
