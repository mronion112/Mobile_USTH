package vn.edu.usth.rssreader.ui.screen.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.fake.FakeCategories
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.screen.saved.components.CloudSyncBar
import vn.edu.usth.rssreader.ui.screen.saved.components.EmptySavedState
import vn.edu.usth.rssreader.ui.screen.saved.components.FolderPill
import vn.edu.usth.rssreader.ui.screen.saved.components.SavedArticleItem
import vn.edu.usth.rssreader.ui.screen.saved.components.SavedSummaryCard
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import java.time.Instant

@Composable
fun SavedScreen(
    modifier: Modifier = Modifier,
    viewModel: SavedViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SavedContent(
        uiState = uiState,
        modifier = modifier,
        onUnsave = viewModel::onUnsave,
        onCategorySelected = viewModel::onCategorySelected,
        onSortModeChange = viewModel::onSortModeChange
    )
}

@Composable
private fun SavedContent(
    uiState: SavedUiState,
    modifier: Modifier = Modifier,
    onUnsave: (Article) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onSortModeChange: (SavedSortMode) -> Unit = {}
) {
    val bookmarked = uiState.bookmarkedArticles
    val isAllSelected = uiState.selectedCategoryId == FakeCategories.All.id

    val filtered = if (isAllSelected) {
        bookmarked
    } else {
        bookmarked.filter { it.category.id == uiState.selectedCategoryId }
    }

    val sortKey: (Article) -> Instant = { it.savedAt ?: it.publishedAt }
    val visibleArticles: List<Article> = when (uiState.sortMode) {
        SavedSortMode.SAVED_NEWEST -> filtered.sortedByDescending(sortKey)
        SavedSortMode.SAVED_OLDEST -> filtered.sortedBy(sortKey)
    }

    val lastSavedAt: Instant = bookmarked
        .mapNotNull { it.savedAt }
        .maxOrNull()
        ?: bookmarked.maxOfOrNull { it.publishedAt }
        ?: Instant.now()

    // Nếu user unsave hết bài của category đang chọn → ViewModel đã reset;
    // nhưng để an toàn nếu ViewModel chưa kịp, guard ở đây cũng không sao.

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        SavedSummaryCard(
            totalCount = bookmarked.size,
            lastSavedAt = lastSavedAt,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(20.dp))

        // ─── Header "DANH MỤC" ───
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DANH MỤC",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        // ─── Pill row (gộp source + bookmark) ───
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = uiState.displayCategories, key = { it.id }) { category ->
                val count = if (category.id == FakeCategories.All.id) {
                    bookmarked.size
                } else {
                    bookmarked.count { it.category.id == category.id }
                }
                FolderPill(
                    category = if (category.id == FakeCategories.All.id) null else category,
                    count = count,
                    selected = category.id == uiState.selectedCategoryId,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── Filter bar (bỏ "Chủ đề", giữ "Lưu gần nhất" static + nút chọn) ───
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortModeChip(
                current = uiState.sortMode,
                onSelect = onSortModeChange
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        // TODO(saved-mode): Multi-select để xóa hàng loạt
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = "Chế độ chọn",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── List ───
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            visibleArticles.forEach { article ->
                SavedArticleItem(
                    article = article,
                    onUnsave = { onUnsave(article) },
                    onShareClick = {
                        // TODO(share): Chia sẻ qua Intent.ACTION_SEND (cần field link trước)
                    },
                    onClick = {
                        // TODO(detail-screen): Mở chi tiết cho article.id = "${article.id}"
                    }
                )
            }
        }

        // ─── 2 loại empty state ───
        when {
            bookmarked.isEmpty() -> {
                Spacer(Modifier.height(16.dp))
                EmptySavedState(modifier = Modifier.padding(horizontal = 16.dp))
            }
            visibleArticles.isEmpty() -> {
                Spacer(Modifier.height(16.dp))
                EmptyFilterState(
                    categoryName = uiState.displayCategories
                        .firstOrNull { it.id == uiState.selectedCategoryId }
                        ?.name ?: "này",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

//        CloudSyncBar(
//            lastSyncedLabel = "Đồng bộ cục bộ",
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SortModeChip(
    current: SavedSortMode,
    onSelect: (SavedSortMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .shadow(2.dp, RoundedCornerShape(50), clip = false)
                .clip(RoundedCornerShape(50))
                .background(SurfaceCard)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = when (current) {
                    SavedSortMode.SAVED_NEWEST -> "Lưu gần nhất"
                    SavedSortMode.SAVED_OLDEST -> "Lưu cũ nhất"
                },
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.24.sp
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer { rotationZ = if (expanded) 180f else 0f }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = SurfaceCard
        ) {
            SortOptionRow(
                label = "Lưu gần nhất",
                selected = current == SavedSortMode.SAVED_NEWEST,
                onClick = {
                    onSelect(SavedSortMode.SAVED_NEWEST)
                    expanded = false
                }
            )
            SortOptionRow(
                label = "Lưu cũ nhất",
                selected = current == SavedSortMode.SAVED_OLDEST,
                onClick = {
                    onSelect(SavedSortMode.SAVED_OLDEST)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun SortOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                color = if (selected) Primary else TextPrimary,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        trailingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        onClick = onClick
    )
}

@Composable
private fun EmptyFilterState(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Chưa có bài nào trong \"$categoryName\"",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Hãy chọn \"Tất cả\" hoặc một danh mục khác.",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380, heightDp = 900)
@Composable
private fun PreviewSavedContent() {
    val sample = FakeArticles.all
    Box(modifier = Modifier.fillMaxSize()) {
        SavedContent(
            uiState = SavedUiState(
                bookmarkedArticles = sample,
                displayCategories = listOf(FakeCategories.All) +
                        sample.map { it.category }.distinctBy { it.id },
                selectedCategoryId = FakeCategories.All.id
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380, heightDp = 500)
@Composable
private fun PreviewSavedContentEmpty() {
    Box(modifier = Modifier.fillMaxSize()) {
        SavedContent(
            uiState = SavedUiState(
                displayCategories = listOf(FakeCategories.All),
                selectedCategoryId = FakeCategories.All.id
            )
        )
    }
}