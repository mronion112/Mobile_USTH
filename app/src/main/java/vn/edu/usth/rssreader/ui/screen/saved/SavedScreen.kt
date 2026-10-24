package vn.edu.usth.rssreader.ui.screen.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.data.fake.FakeArticles
import vn.edu.usth.rssreader.data.fake.FakeExploreArticles
import vn.edu.usth.rssreader.data.fake.FakeSavedData
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.data.model.Category
import vn.edu.usth.rssreader.ui.screen.saved.components.FeedSyncBar
import vn.edu.usth.rssreader.ui.screen.saved.components.EmptySavedState
import vn.edu.usth.rssreader.ui.screen.saved.components.FolderPill
import vn.edu.usth.rssreader.ui.screen.saved.components.SavedArticleItem
import vn.edu.usth.rssreader.ui.screen.saved.components.SavedSummaryCard
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import java.time.Instant

private data class FolderEntry(
    val id: String,
    val category: Category?,
    val count: Int
)

@Composable
fun SavedScreen(
    modifier: Modifier = Modifier,
    onArticleClick: (String) -> Unit = {}
) {

    var selectedFolderId by rememberSaveable { mutableStateOf("all") }

    val setSaver = listSaver<Set<String>, String>(
        save = { it.toList() },
        restore = { it.toSet() }
    )
    var removedIds by rememberSaveable(stateSaver = setSaver) {
        mutableStateOf(setOf<String>())
    }

    val savedArticles: List<Article> = remember(removedIds) {
        (FakeArticles.all + FakeExploreArticles.results)
            .filter { it.id in FakeSavedData.savedArticleIds && it.id !in removedIds }
    }

    val folders: List<FolderEntry> = remember(savedArticles) {
        buildList {
            add(FolderEntry("all", null, savedArticles.size))
            savedArticles
                .groupBy { it.category.id }
                .forEach { (_, list) ->
                    add(
                        FolderEntry(
                            id = list.first().category.id,
                            category = list.first().category,
                            count = list.size
                        )
                    )
                }
        }
    }

    val visibleArticles: List<Article> =
        if (selectedFolderId == "all") savedArticles
        else savedArticles.filter { it.category.id == selectedFolderId }

    val lastSavedAt: Instant = savedArticles
        .maxOfOrNull { it.publishedAt }
        ?: Instant.now()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        // Hero summary card
        SavedSummaryCard(
            totalCount = savedArticles.size,
            lastSavedAt = lastSavedAt,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Folder section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BỘ SƯU TẬP",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )

        }

        Spacer(Modifier.height(8.dp))

        // Folder pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = folders, key = { it.id }) { folder ->
                FolderPill(
                    category = folder.category,
                    count = folder.count,
                    selected = folder.id == selectedFolderId,
                    onClick = { selectedFolderId = folder.id }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Filter bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StaticFilterChip(icon = Icons.Filled.Schedule, label = "Lưu gần nhất")
                StaticFilterChip(icon = Icons.Filled.Label, label = "Chủ đề")
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* no-op */ },
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

        // Article list
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            visibleArticles.forEach { article ->
                AnimatedVisibility(
                    visible = article.id !in removedIds,
                    exit = fadeOut(tween(250)) + shrinkVertically(tween(350))
                ) {
                    SavedArticleItem(
                        article = article,
                        onUnsave = { removedIds = removedIds + article.id },
                        onShareClick = { /* no-op */ },
                        onClick = { onArticleClick(article.id) }
                    )
                }
            }
        }

        // Empty state
        if (visibleArticles.isEmpty()) {
            Spacer(Modifier.height(16.dp))
            EmptySavedState(modifier = Modifier.padding(horizontal = 16.dp))
        }

        Spacer(Modifier.height(20.dp))

        // On-device feed sync bar
        FeedSyncBar(modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StaticFilterChip(
    icon: ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(50), clip = false)
            .clip(RoundedCornerShape(50))
            .background(SurfaceCard)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
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
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, widthDp = 380, heightDp = 900)
@Composable
private fun PreviewSavedScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        SavedScreen()
    }
}