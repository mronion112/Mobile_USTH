package vn.edu.usth.rssreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.usth.rssreader.core.designsystem.EditorialPulseColors
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.data.repository.RssSourceRepository
import vn.edu.usth.rssreader.feature.s07_add_source.AddRssSourceModalSheet

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileUSTHTheme {
                AppShell()
            }
        }
    }
}

private val Teal = Color(0xFF0F766E)
private val MutedSlate = Color(0xFF94A3B8)
private val White = Color.White

private enum class Destination(
    @param:StringRes val label: Int,
    val icon: ImageVector,
) {
    Home(R.string.nav_home, Icons.Outlined.Home),
    Explore(R.string.nav_explore, Icons.Outlined.Explore),
    Saved(R.string.nav_saved, Icons.Outlined.BookmarkBorder),
    Rss(R.string.nav_rss, Icons.Outlined.RssFeed),
}

@Composable
private fun AppShell() {
    var selectedDestination by rememberSaveable { mutableStateOf(Destination.Home) }
    var showAddRssSheet by rememberSaveable { mutableStateOf(false) }
    val sources by RssSourceRepository.shared.sources.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = EditorialPulseColors.SurfaceCanvas,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(64.dp),
                containerColor = White,
                tonalElevation = 8.dp,
            ) {
                Destination.entries.forEach { destination ->
                    val isSelected = destination == selectedDestination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = stringResource(destination.label),
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.label),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal,
                            selectedTextColor = Teal,
                            unselectedIconColor = MutedSlate,
                            unselectedTextColor = MutedSlate,
                            indicatorColor = Color.Transparent,
                        ),
                    )
                }
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(EditorialPulseColors.SurfaceCanvas),
        ) {
            if (selectedDestination == Destination.Rss) {
                RssSourcesHostSection(
                    sources = sources,
                    onAddSourceClicked = { showAddRssSheet = true },
                )
            }
        }

        if (showAddRssSheet) {
            AddRssSourceModalSheet(
                onDismissRequest = { showAddRssSheet = false },
                onSourceAdded = { showAddRssSheet = false },
            )
        }
    }
}

@Composable
private fun RssSourcesHostSection(
    sources: List<RssSource>,
    onAddSourceClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Nguồn tin RSS",
                    color = EditorialPulseColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(EditorialPulseColors.PrimaryTealSoft)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "${sources.size} nguồn",
                        color = EditorialPulseColors.PrimaryTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Button(
                onClick = onAddSourceClicked,
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditorialPulseColors.PrimaryTeal,
                    contentColor = EditorialPulseColors.SurfaceCard,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                modifier = Modifier.height(44.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Thêm nguồn",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Quản lý các luồng cấp tin RSS tự động từ Báo Dân trí và các danh mục tin tức thời sự số.",
            color = EditorialPulseColors.TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(
                items = sources,
                key = { it.id },
            ) { source ->
                RssSourceSummaryCard(source = source)
            }
        }
    }
}

@Composable
private fun RssSourceSummaryCard(
    source: RssSource,
    modifier: Modifier = Modifier,
) {
    val isFetching = source.status == RssSource.STATUS_FETCHING
    val statusDotColor = if (isFetching) EditorialPulseColors.SecondaryAmber else EditorialPulseColors.StatusActive
    val statusLabel = if (isFetching) "Đang nạp dữ liệu..." else "Đang hoạt động"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EditorialPulseColors.SurfaceCard)
            .border(1.dp, EditorialPulseColors.BorderSubtle, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = source.name,
                color = EditorialPulseColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(EditorialPulseColors.SurfaceCardSubtle)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = source.category.name,
                    color = EditorialPulseColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Text(
            text = source.rssUrl,
            color = EditorialPulseColors.TextMuted,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusDotColor),
            )
            Text(
                text = statusLabel,
                color = statusDotColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MobileUSTHTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Teal,
            surface = White,
            background = White,
        ),
        typography = Typography(),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppShellPreview() {
    MobileUSTHTheme {
        Surface {
            AppShell()
        }
    }
}
