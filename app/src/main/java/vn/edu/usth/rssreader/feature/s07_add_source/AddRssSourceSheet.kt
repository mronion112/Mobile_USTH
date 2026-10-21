package vn.edu.usth.rssreader.feature.s07_add_source

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.edu.usth.rssreader.core.designsystem.EditorialPulseColors
import vn.edu.usth.rssreader.core.designsystem.EditorialPulseTheme
import vn.edu.usth.rssreader.core.model.FetchError
import vn.edu.usth.rssreader.core.model.RssSource
import vn.edu.usth.rssreader.core.model.ValidationPreview
import vn.edu.usth.rssreader.data.repository.RssSourceRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRssSourceModalSheet(
    onDismissRequest: () -> Unit,
    onSourceAdded: (RssSource) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRssSourceViewModel = viewModel(factory = AddRssSourceViewModel.provideFactory()),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = EditorialPulseColors.SurfaceCard,
        scrimColor = Color.Black.copy(alpha = 0.45f),
        dragHandle = null,
        modifier = modifier,
    ) {
        AddRssSourceSheetContent(
            uiState = uiState,
            onNameChanged = viewModel::onNameChanged,
            onRssUrlChanged = viewModel::onRssUrlChanged,
            onValidateClicked = viewModel::validateRssUrl,
            onCategorySelected = viewModel::onCategorySelected,
            onCancelClicked = onDismissRequest,
            onSaveClicked = {
                viewModel.saveSource { created ->
                    viewModel.resetForm()
                    onSourceAdded(created)
                }
            },
        )
    }
}

@Composable
fun AddRssSourceSheetContent(
    uiState: AddRssSourceUiState,
    onNameChanged: (String) -> Unit,
    onRssUrlChanged: (String) -> Unit,
    onValidateClicked: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onCancelClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = EditorialPulseColors.SurfaceCard,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EditorialPulseColors.PrimaryTealSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddLink,
                        contentDescription = null,
                        tint = EditorialPulseColors.PrimaryTeal,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = "Thêm luồng RSS mới",
                    color = EditorialPulseColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            IconButton(
                onClick = onCancelClicked,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(EditorialPulseColors.SurfaceCardSubtle),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Đóng hộp thoại thêm nguồn RSS",
                    tint = EditorialPulseColors.TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        FieldLabel(text = "Tên hiển thị nguồn")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.nameInput,
            onValueChange = onNameChanged,
            enabled = !uiState.isSaving,
            singleLine = true,
            placeholder = {
                Text(
                    text = "Ví dụ: Dân trí — Thể thao",
                    color = EditorialPulseColors.TextMuted,
                    fontSize = 14.sp,
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = editorialTextFieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(text = "Đường dẫn RSS (URL)")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.rssUrlInput,
            onValueChange = onRssUrlChanged,
            enabled = !uiState.isSaving,
            singleLine = true,
            placeholder = {
                Text(
                    text = "https://dantri.com.vn/rss/...",
                    color = EditorialPulseColors.TextMuted,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailingIcon = {
                ValidateInlineButton(
                    isLoading = uiState.isValidating,
                    enabled = uiState.canValidate,
                    onClick = onValidateClicked,
                    modifier = Modifier.padding(end = 6.dp),
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = editorialTextFieldColors(isError = uiState.error != null),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (uiState.canValidate) onValidateClicked() },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        )

        AnimatedVisibility(visible = uiState.validationPreview != null) {
            uiState.validationPreview?.let { preview ->
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    ValidFeedPreviewCard(preview = preview)
                }
            }
        }

        AnimatedVisibility(visible = uiState.error != null) {
            uiState.error?.let { error ->
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    InvalidFeedErrorCard(
                        error = error,
                        onRetryClicked = onValidateClicked,
                        canRetry = uiState.canValidate,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        FieldLabel(text = "Chuyên mục mặc định")
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                items = uiState.categories,
                key = { it.id },
            ) { category ->
                val isSelected = category.id == uiState.selectedCategoryId
                CategoryPillChip(
                    name = category.name,
                    selected = isSelected,
                    enabled = !uiState.isSaving,
                    onClick = { onCategorySelected(category.id) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onCancelClicked,
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditorialPulseColors.SurfaceCardSubtle,
                    contentColor = EditorialPulseColors.TextSecondary,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
            ) {
                Text(
                    text = "Hủy",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Button(
                onClick = onSaveClicked,
                enabled = uiState.canSave,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditorialPulseColors.PrimaryTeal,
                    contentColor = EditorialPulseColors.SurfaceCard,
                    disabledContainerColor = EditorialPulseColors.SurfaceCardSubtle,
                    disabledContentColor = EditorialPulseColors.TextMuted,
                ),
                modifier = Modifier
                    .weight(1.4f)
                    .height(48.dp),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = EditorialPulseColors.SurfaceCard,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Thêm nguồn",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = EditorialPulseColors.TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun ValidateInlineButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = EditorialPulseColors.SurfaceCard,
        border = BorderStroke(1.dp, EditorialPulseColors.BorderSubtle),
        shadowElevation = 1.dp,
        modifier = modifier.height(36.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = EditorialPulseColors.PrimaryTeal,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp),
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Verified,
                    contentDescription = null,
                    tint = if (enabled) EditorialPulseColors.PrimaryTeal else EditorialPulseColors.TextMuted,
                    modifier = Modifier.size(15.dp),
                )
            }
            Text(
                text = "Kiểm tra",
                color = if (enabled) EditorialPulseColors.PrimaryTeal else EditorialPulseColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ValidFeedPreviewCard(
    preview: ValidationPreview,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EditorialPulseColors.PrimaryTealSurface)
            .border(
                width = 1.dp,
                color = EditorialPulseColors.PrimaryTealSoft,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = EditorialPulseColors.StatusActive,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "RSS feed hợp lệ — Đã phát hiện luồng tin",
                color = EditorialPulseColors.StatusActive,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(EditorialPulseColors.SurfaceCard)
                .border(1.dp, EditorialPulseColors.BorderSubtle, RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Tiêu đề luồng:",
                    color = EditorialPulseColors.TextSecondary,
                    fontSize = 12.sp,
                )
                Text(
                    text = preview.detectedTitle,
                    color = EditorialPulseColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Nguồn phát hành:",
                    color = EditorialPulseColors.TextSecondary,
                    fontSize = 12.sp,
                )
                Text(
                    text = preview.detectedSiteName,
                    color = EditorialPulseColors.PrimaryTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun InvalidFeedErrorCard(
    error: FetchError,
    onRetryClicked: () -> Unit,
    canRetry: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EditorialPulseColors.StatusErrorSoft)
            .border(
                width = 1.dp,
                color = EditorialPulseColors.StatusErrorBorder,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = EditorialPulseColors.StatusError,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = error.message,
                color = EditorialPulseColors.StatusError,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (error.code == FetchError.RSS_FEED_INVALID) {
            Text(
                text = "Vui lòng kiểm tra:\n• Đường dẫn URL chính xác\n• URL trỏ tới luồng RSS/Atom công khai",
                color = EditorialPulseColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        }

        if (canRetry && error.code != FetchError.DUPLICATE_SOURCE) {
            Text(
                text = "Thử kiểm tra lại",
                color = EditorialPulseColors.PrimaryTeal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = onRetryClicked)
                    .padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun CategoryPillChip(
    name: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) {
        EditorialPulseColors.PrimaryTeal
    } else {
        EditorialPulseColors.SurfaceCardSubtle
    }
    val textColor = if (selected) {
        EditorialPulseColors.SurfaceCard
    } else {
        EditorialPulseColors.TextSecondary
    }

    Box(
        modifier = modifier
            .heightIn(min = 38.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun editorialTextFieldColors(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = EditorialPulseColors.SurfaceCard,
    unfocusedContainerColor = EditorialPulseColors.SurfaceCardSubtle,
    disabledContainerColor = EditorialPulseColors.SurfaceCardSubtle,
    errorContainerColor = EditorialPulseColors.StatusErrorSoft,
    focusedBorderColor = if (isError) EditorialPulseColors.StatusError else EditorialPulseColors.BorderFocus,
    unfocusedBorderColor = if (isError) EditorialPulseColors.StatusErrorBorder else EditorialPulseColors.BorderSubtle,
    errorBorderColor = EditorialPulseColors.StatusError,
    focusedTextColor = EditorialPulseColors.TextPrimary,
    unfocusedTextColor = EditorialPulseColors.TextPrimary,
    cursorColor = EditorialPulseColors.PrimaryTeal,
)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AddRssSourceSheetInitialPreview() {
    EditorialPulseTheme {
        val categories = RssSourceRepository.defaultCategories()
        AddRssSourceSheetContent(
            uiState = AddRssSourceUiState(
                categories = categories,
                selectedCategoryId = categories.first().id,
            ),
            onNameChanged = {},
            onRssUrlChanged = {},
            onValidateClicked = {},
            onCategorySelected = {},
            onCancelClicked = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AddRssSourceSheetValidPreview() {
    EditorialPulseTheme {
        val categories = RssSourceRepository.defaultCategories()
        val worldCategory = categories.first { it.slug == "the-gioi" }
        AddRssSourceSheetContent(
            uiState = AddRssSourceUiState(
                nameInput = "Dân trí — Thế giới",
                rssUrlInput = "https://dantri.com.vn/rss/the-gioi.rss",
                categories = categories,
                selectedCategoryId = worldCategory.id,
                validationPreview = ValidationPreview(
                    valid = true,
                    detectedTitle = "Dân trí - Thế giới",
                    detectedSiteName = "Dân trí",
                    suggestedCategory = worldCategory,
                ),
            ),
            onNameChanged = {},
            onRssUrlChanged = {},
            onValidateClicked = {},
            onCategorySelected = {},
            onCancelClicked = {},
            onSaveClicked = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AddRssSourceSheetErrorPreview() {
    EditorialPulseTheme {
        val categories = RssSourceRepository.defaultCategories()
        AddRssSourceSheetContent(
            uiState = AddRssSourceUiState(
                nameInput = "Nguồn thử nghiệm",
                rssUrlInput = "https://example.com/not-a-feed",
                categories = categories,
                selectedCategoryId = categories.first().id,
                error = FetchError(
                    code = FetchError.RSS_FEED_INVALID,
                    message = "RSS URL không hợp lệ hoặc không thể truy cập",
                ),
            ),
            onNameChanged = {},
            onRssUrlChanged = {},
            onValidateClicked = {},
            onCategorySelected = {},
            onCancelClicked = {},
            onSaveClicked = {},
        )
    }
}
