package vn.edu.usth.rssreader.ui.screen.rss

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.edu.usth.rssreader.data.model.RssSourceStatus
import vn.edu.usth.rssreader.ui.screen.rss.components.AddRssSourceSheet
import vn.edu.usth.rssreader.ui.screen.rss.components.RemoveSourceConfirmationDialog
import vn.edu.usth.rssreader.ui.screen.rss.components.RssSourceCard
import vn.edu.usth.rssreader.ui.theme.BorderSubtle
import vn.edu.usth.rssreader.ui.theme.PrimaryContainer
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.StatusSuccess
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

/** S06 source management and the S07 add-source entry sheet. */
@Composable
fun RssScreen(
    modifier: Modifier = Modifier,
    viewModel: RssSourcesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { message -> snackbarHostState.showSnackbar(message) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        when {
            uiState.isLoading -> RssSourcesLoading()
            uiState.loadError != null -> RssSourcesLoadError(
                message = uiState.loadError.orEmpty(),
                onRetry = viewModel::reloadSources,
                onRestoreDefault = viewModel::restoreBundledCatalog
            )
            uiState.sources.isEmpty() -> EmptySources(onAddSource = viewModel::openAddSource)
            else -> SourcesContent(
                state = uiState,
                onAddSource = viewModel::openAddSource,
                onRefresh = viewModel::refresh,
                onRemove = viewModel::requestRemoval
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }

    uiState.pendingRemoval?.let { request ->
        RemoveSourceConfirmationDialog(
            sourceName = request.name,
            isRemoving = uiState.removingSourceId == request.id,
            errorMessage = uiState.removalError,
            onDismiss = viewModel::dismissRemoval,
            onConfirm = viewModel::confirmRemoval
        )
    }

    uiState.addSource?.let { state ->
        AddRssSourceSheet(
            state = state,
            onNameChange = viewModel::updateNewSourceName,
            onUrlChange = viewModel::updateNewSourceUrl,
            onCategorySelected = viewModel::selectNewSourceCategory,
            onValidate = viewModel::validateNewSource,
            onSave = viewModel::saveNewSource,
            onDismiss = viewModel::dismissAddSource
        )
    }
}

@Composable
private fun SourcesContent(
    state: RssSourcesUiState,
    onAddSource: () -> Unit,
    onRefresh: (Long) -> Unit,
    onRemove: (Long) -> Unit
) {
    val activeCount = state.sources.count { it.status == RssSourceStatus.ACTIVE }
    val fetchingCount = state.sources.count { it.status == RssSourceStatus.FETCHING }
    val errorCount = state.sources.count { it.status == RssSourceStatus.ERROR }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SourceHeader(
                sourceCount = state.sources.size,
                onAddSource = onAddSource
            )
        }
        item {
            Text(
                text = "Quản lý các luồng cập tin RSS tự động từ Báo Dân trí và các danh mục tin tức thời sự số.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        item {
            SourceSummary(
                activeCount = activeCount,
                fetchingCount = fetchingCount,
                errorCount = errorCount
            )
        }
        items(items = state.sources, key = { it.id }) { source ->
            RssSourceCard(
                source = source,
                onRefresh = onRefresh,
                onRemove = onRemove
            )
        }
    }
}

@Composable
private fun SourceHeader(sourceCount: Int, onAddSource: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Nguồn tin RSS",
            modifier = Modifier.weight(1f),
            color = TextPrimary,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.4).sp
        )
        SourceCountBadge(sourceCount)
        Spacer(Modifier.width(12.dp))
        Button(
            onClick = onAddSource,
            modifier = Modifier.height(44.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryContainer,
                contentColor = SurfaceCard
            )
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Thêm nguồn",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SourceCountBadge(sourceCount: Int) {
    Surface(
        color = PrimaryContainer.copy(alpha = 0.12f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
    ) {
        Text(
            text = "$sourceCount nguồn",
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            color = PrimaryContainer,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SourceSummary(activeCount: Int, fetchingCount: Int, errorCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.55f)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryCell(
                modifier = Modifier.weight(1f),
                count = activeCount,
                label = "Hoạt động",
                color = StatusSuccess,
                icon = Icons.Filled.RssFeed
            )
            SummaryCell(
                modifier = Modifier.weight(1f),
                count = fetchingCount,
                label = "Đang nạp",
                color = PrimaryContainer,
                icon = Icons.Filled.Sync
            )
            SummaryCell(
                modifier = Modifier.weight(1f),
                count = errorCount,
                label = "Lỗi nguồn",
                color = StatusError,
                icon = Icons.Filled.ErrorOutline
            )
        }
    }
}

@Composable
private fun SummaryCell(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .background(SurfaceCardSubtle)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = count.toString(),
                color = color,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptySources(onAddSource: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                .background(PrimaryContainer.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.RssFeed,
                contentDescription = null,
                tint = PrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Chưa có nguồn RSS",
            color = TextPrimary,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Thêm một nguồn để theo dõi các bài viết mới nhất.",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onAddSource,
            modifier = Modifier.height(44.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryContainer,
                contentColor = SurfaceCard
            )
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Thêm nguồn RSS", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RssSourcesLoadError(
    message: String,
    onRetry: () -> Unit,
    onRestoreDefault: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = StatusError,
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Không thể mở nguồn RSS",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.height(44.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryContainer,
                contentColor = SurfaceCard
            )
        ) {
            Text("Thử lại", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onRestoreDefault,
            modifier = Modifier.height(44.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text("Khôi phục mặc định", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Khôi phục sẽ thay danh sách nguồn đã lưu trên thiết bị bằng danh sách mặc định.",
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RssSourcesLoading() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.58f)
                    .height(28.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(SurfaceCardSubtle)
            )
        }
        items(3) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp),
                color = SurfaceCard,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.5f))
            ) {}
        }
    }
}
