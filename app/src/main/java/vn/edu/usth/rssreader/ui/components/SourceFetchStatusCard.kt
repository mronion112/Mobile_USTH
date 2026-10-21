package vn.edu.usth.rssreader.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.data.model.RssSource
import vn.edu.usth.rssreader.data.model.RssSourceStatus
import vn.edu.usth.rssreader.feature.status.toFetchStatusUi
import vn.edu.usth.rssreader.ui.theme.ActionTeal
import vn.edu.usth.rssreader.ui.theme.BorderSubtle
import vn.edu.usth.rssreader.ui.theme.Canvas
import vn.edu.usth.rssreader.ui.theme.CardSubtle
import vn.edu.usth.rssreader.ui.theme.MobileUSTHTheme
import vn.edu.usth.rssreader.ui.theme.StatusActive
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import vn.edu.usth.rssreader.ui.theme.White

@Composable
fun SourceFetchStatusCard(
    source: RssSource,
    onRetry: (Long) -> Unit,
    modifier: Modifier = Modifier,
    now: Instant = Instant.now(),
) {
    val status = source.toFetchStatusUi(now = now)
    if (!status.isVisible) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSubtle),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RssFeed,
                        contentDescription = null,
                        tint = ActionTeal,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = source.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = source.rssUrl,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (status.isFetching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = ActionTeal,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (source.status == RssSourceStatus.ERROR) {
                                    StatusError
                                } else {
                                    StatusActive
                                },
                            ),
                    )
                }
                Text(
                    text = status.label,
                    color = when (source.status) {
                        RssSourceStatus.ERROR -> StatusError
                        RssSourceStatus.FETCHING -> ActionTeal
                        else -> StatusActive
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "• ${status.lastFetchedLabel}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                )
            }

            status.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                )
            }

            if (status.canRetry) {
                Button(
                    onClick = { onRetry(source.id) },
                    modifier = Modifier
                        .align(Alignment.End)
                        .heightIn(min = 44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActionTeal,
                        contentColor = White,
                    ),
                ) {
                    Text(
                        text = "Thử lại",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview(name = "S09 Source statuses", widthDp = 390, heightDp = 700)
@Composable
private fun SourceFetchStatusCardsPreview() {
    val previewNow = Instant.parse("2026-10-21T09:00:00Z")
    MobileUSTHTheme {
        Column(
            modifier = Modifier
                .background(Canvas)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RssSourceStatus.entries
                .filterNot { it == RssSourceStatus.DISABLED }
                .forEachIndexed { index, status ->
                    SourceFetchStatusCard(
                        source = previewSource(index.toLong(), status),
                        onRetry = {},
                        now = previewNow,
                    )
                }
        }
    }
}

private fun previewSource(id: Long, status: RssSourceStatus) = RssSource(
    id = id,
    name = "Dân trí — Công nghệ",
    rssUrl = "https://dantri.com.vn/rss/suc-manh-so.rss",
    category = Category(id = "cong-nghe", name = "Công nghệ", colorIndex = 2),
    status = status,
    lastFetchedAt = Instant.parse("2026-10-21T08:25:00Z"),
    lastError = if (status == RssSourceStatus.ERROR) {
        "Không thể kết nối nguồn RSS."
    } else {
        null
    },
    createdAt = Instant.parse("2026-10-01T08:00:00Z"),
)
