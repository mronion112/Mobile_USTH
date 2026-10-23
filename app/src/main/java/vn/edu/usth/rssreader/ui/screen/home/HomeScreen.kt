package vn.edu.usth.rssreader.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.edu.usth.rssreader.ui.screen.home.components.CategoryStrip
import vn.edu.usth.rssreader.ui.screen.home.components.FeaturedSection
import vn.edu.usth.rssreader.ui.screen.home.components.GreetingRow
import vn.edu.usth.rssreader.ui.screen.home.components.HomeSearchBar
import vn.edu.usth.rssreader.ui.screen.home.components.LatestSection
import vn.edu.usth.rssreader.ui.screen.home.components.RssStatusBar
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onSearchClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))

        // ─── Header + Search + Category (luôn hiển thị) ───
        uiState.reader?.let { reader ->
            GreetingRow(
                greeting = uiState.greeting,
                reader = reader,
                isSyncing = uiState.isSyncing,
                hasUnreadNotifications = uiState.hasUnreadNotifications,
                onSyncClick = viewModel::onSyncClick,
                onNotificationClick = viewModel::onNotificationClick
            )
        }

        Spacer(Modifier.height(20.dp))

        HomeSearchBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onSearchClick
        )

        Spacer(Modifier.height(20.dp))

        if (uiState.categories.size > 1) {
            CategoryStrip(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onCategorySelected = viewModel::onCategorySelected,
                syncedFeedCount = uiState.syncedFeedCount
            )
        }

        Spacer(Modifier.height(20.dp))

        // ─── Content theo trạng thái ───
        when {
            uiState.isInitialLoading -> LoadingState()

            !uiState.hasAnySource -> NoSourceState()

            uiState.refreshError != null &&
                    uiState.featuredArticle == null &&
                    uiState.latestArticles.isEmpty() -> ErrorState(uiState.refreshError!!)

            uiState.featuredArticle == null && uiState.latestArticles.isEmpty() -> EmptyFilterState()

            else -> {
                uiState.featuredArticle?.let { article ->
                    FeaturedSection(
                        article = article,
                        bookmarked = uiState.savedArticleIds.contains(article.id),
                        onBookmarkToggle = { bookmarked ->
                            viewModel.onToggleBookmark(article, bookmarked)
                        },
                        onCardClick = {
                            // TODO(detail-screen): Mở màn hình chi tiết cho article.id = "${article.id}"

                        },
                        onReadNowClick = {
                            // TODO(detail-screen): Giống onCardClick; có thể scroll tới phần nội dung chính
                            //   sau khi mở detail.
                            // articleId = "${article.id}"
                        },
                        onSeeAllClick = {
                            // TODO(all-articles-screen): Mở màn hình "Tất cả bài nổi bật".

                        }
                    )
                    Spacer(Modifier.height(20.dp))
                }

                if (uiState.latestArticles.isNotEmpty()) {
                    LatestSection(
                        articles = uiState.latestArticles,
                        savedIds = uiState.savedArticleIds,
                        isRefreshing = uiState.isRefreshing,
                        onRefreshClick = viewModel::onRefreshClick,
                        onBookmarkToggle = viewModel::onToggleBookmark,
                        onArticleClick = { articleId ->
                            // TODO(detail-screen): Mở chi tiết cho articleId.

                        },
                        onSeeMoreClick = {
                            // TODO(all-articles-screen): Mở màn hình "Tất cả bài mới nhất".

                        }
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
        }

        // ─── Status bar luôn hiển thị ───
        RssStatusBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            online = uiState.hasAnySource && uiState.refreshError == null
        )

        Spacer(Modifier.height(32.dp))
    }
}

// ─── State composables ─────────────────────────────────────

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                color = Primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Đang tải tin tức…",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun NoSourceState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.RssFeed,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Chưa có nguồn RSS nào",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Mở tab \"Nguồn RSS\" ở thanh điều hướng để thêm nguồn tin bạn muốn theo dõi.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Không tải được tin tức",
            color = StatusError,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Kéo xuống hoặc bấm nút \"Làm mới\" trong tab Nguồn RSS để thử lại.",
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyFilterState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Không có bài nào trong chuyên mục này",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Hãy chọn \"Tất cả\" hoặc một chuyên mục khác.",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(onSearchClick = {})
    }
}