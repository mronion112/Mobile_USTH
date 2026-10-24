package vn.edu.usth.rssreader.ui.screen.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.edu.usth.rssreader.ui.screen.home.components.CategoryStrip
import vn.edu.usth.rssreader.ui.screen.home.components.FeaturedSection
import vn.edu.usth.rssreader.ui.screen.home.components.GreetingRow
import vn.edu.usth.rssreader.ui.screen.home.components.HomeSearchBar
import vn.edu.usth.rssreader.ui.screen.home.components.LatestSection
import vn.edu.usth.rssreader.ui.screen.home.components.RssStatusBar
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onArticleClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))

        // Greeting
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

        // Search
        HomeSearchBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it }
        )

        Spacer(Modifier.height(20.dp))

        // Category strip
        CategoryStrip(
            categories = uiState.categories,
            selectedCategoryId = uiState.selectedCategoryId,
            onCategorySelected = viewModel::onCategorySelected
        )

        Spacer(Modifier.height(20.dp))

        // Featured section
        FeaturedSection(
            article = uiState.featuredArticle,
            bookmarked = uiState.featuredArticle?.let { uiState.savedArticleIds.contains(it.id) } ?: false,
            onBookmarkToggle = { bookmarked ->
                uiState.featuredArticle?.let { viewModel.onToggleBookmark(it.id, bookmarked) }
            },
            onCardClick = {
                uiState.featuredArticle?.let { onArticleClick(it.id) }
            },
            onReadNowClick = {
                uiState.featuredArticle?.let { onArticleClick(it.id) }
            }
        )

        Spacer(Modifier.height(20.dp))

        // Latest section
        LatestSection(
            articles = uiState.latestArticles,
            savedIds = uiState.savedArticleIds,
            isRefreshing = uiState.isRefreshing,
            onRefreshClick = viewModel::onRefreshClick,
            onBookmarkToggle = viewModel::onToggleBookmark,
            onArticleClick = onArticleClick
        )

        Spacer(Modifier.height(20.dp))

        // RSS status bar
        RssStatusBar(
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen()
    }
}