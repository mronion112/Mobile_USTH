package vn.edu.usth.rssreader.ui.screen.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.components.article.CategoryPill
import vn.edu.usth.rssreader.ui.screen.explore.components.ExploreSearchBar
import vn.edu.usth.rssreader.ui.screen.explore.components.FilterBadgesRow
import vn.edu.usth.rssreader.ui.screen.explore.components.LoadMoreButton
import vn.edu.usth.rssreader.ui.screen.explore.components.SearchResultItem
import vn.edu.usth.rssreader.ui.screen.explore.components.TrendingTopicsSection
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    focusSearchOnEnter: Boolean = false,
    onSearchFocused: () -> Unit = {},
    onArticleClick: (String) -> Unit = {},
    viewModel: ExploreViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ExploreContent(
        uiState = uiState,
        modifier = modifier,
        focusSearchOnEnter = focusSearchOnEnter,
        onSearchFocused = onSearchFocused,
        onArticleClick = onArticleClick,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onLoadMore = viewModel::onLoadMore,
        onBookmarkToggle = viewModel::onToggleBookmark
    )
}

@Composable
private fun ExploreContent(
    uiState: ExploreUiState,
    modifier: Modifier = Modifier,
    focusSearchOnEnter: Boolean = false,
    onSearchFocused: () -> Unit = {},
    onArticleClick: (String) -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onBookmarkToggle: (Article, Boolean) -> Unit = { _, _ -> }
) {
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(focusSearchOnEnter) {
        if (focusSearchOnEnter) {
            delay(150)
            runCatching { searchFocusRequester.requestFocus() }
            onSearchFocused()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        ExploreSearchBar(
            value = uiState.searchQuery,
            onValueChange = onQueryChange,
            onFilterClick = { },
            focusRequester = searchFocusRequester,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        // Category pills — giống Home
        if (uiState.displayCategories.size > 1) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = uiState.displayCategories, key = { it.id }) { category ->
                    CategoryPill(
                        category = category,
                        selected = category.id == uiState.selectedCategoryId,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        when {
            uiState.isLoading -> ExploreLoading()

            !uiState.hasAnySource -> ExploreNoSource()

            else -> {
                // Trending — chỉ khi không search
                if (uiState.searchQuery.isBlank() && uiState.trendingArticles.isNotEmpty()) {
                    TrendingTopicsSection(
                        articles = uiState.trendingArticles,
                        onSeeAllClick = {
                            // TODO(all-articles-screen)
                        },
                        onArticleClick = onArticleClick
                    )
                    Spacer(Modifier.height(16.dp))
                }

                FilterBadgesRow(resultCount = uiState.filteredArticles.size)

                Spacer(Modifier.height(12.dp))

                if (uiState.visibleArticles.isEmpty()) {
                    ExploreEmpty(
                        query = uiState.searchQuery,
                        onClearSearch = { onQueryChange("") }
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.visibleArticles.forEach { article ->
                            SearchResultItem(
                                article = article,
                                keyword = uiState.searchQuery,
                                bookmarked = uiState.bookmarkedIds.contains(article.id),
                                onBookmarkToggle = { bookmarked ->
                                    onBookmarkToggle(article, bookmarked)
                                },
                                onShareClick = {
                                    // TODO(share)
                                },
                                onClick = { onArticleClick(article.id) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    if (uiState.hasMore) {
                        LoadMoreButton(
                            remainingCount = uiState.remainingCount,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClick = onLoadMore
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─── State composables ─────────────────────────────────────

@Composable
private fun ExploreLoading() {
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
                text = "Đang tải dữ liệu…",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ExploreNoSource() {
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
            text = "Mở tab \"Nguồn RSS\" để thêm nguồn tin trước khi tìm kiếm.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ExploreEmpty(
    query: String,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (query.isNotBlank()) {
            Text(
                text = "Không tìm thấy bài nào khớp với \"$query\"",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "App chỉ tìm trong các bài đã tải từ RSS (khoảng 20–50 bài mới nhất mỗi nguồn).",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onClearSearch,
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = SurfaceCard
                )
            ) {
                Text("Xóa tìm kiếm", fontWeight = FontWeight.SemiBold)
            }
        } else {
            Text(
                text = "Chưa có bài nào trong chuyên mục này",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Hãy chọn \"Tất cả\" hoặc một chuyên mục khác.",
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Preview ───────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380, heightDp = 900)
@Composable
private fun PreviewExploreContent() {
    val sample = FakeArticles.all
    Box(modifier = Modifier.fillMaxSize()) {
        ExploreContent(
            uiState = ExploreUiState(
                searchQuery = "",
                selectedCategoryId = FakeCategories.All.id,
                allArticles = sample,
                displayCategories = listOf(FakeCategories.All) +
                        sample.map { it.category }.distinctBy { it.id },
                filteredArticles = sample,
                visibleArticles = sample,
                trendingArticles = sample.take(3),
                hasAnySource = true,
                isLoading = false
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380, heightDp = 600)
@Composable
private fun PreviewExploreEmpty() {
    Box(modifier = Modifier.fillMaxSize()) {
        ExploreContent(
            uiState = ExploreUiState(
                searchQuery = "blockchain",
                displayCategories = listOf(FakeCategories.All),
                hasAnySource = true,
                isLoading = false
            )
        )
    }
}
