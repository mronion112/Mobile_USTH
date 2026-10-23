package vn.edu.usth.rssreader.ui.screen.rss.components

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.ui.screen.rss.AddSourceUiState
import vn.edu.usth.rssreader.ui.theme.PrimaryContainer
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.StatusSuccess
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRssSourceSheet(
    state: AddSourceUiState,
    onNameChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onValidate: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isBusy = state.isValidating || state.isSaving
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Thêm nguồn RSS",
                color = TextPrimary,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Kiểm tra liên kết RSS trước khi lưu vào danh sách của bạn.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            SourceInput(
                value = state.name,
                onValueChange = onNameChange,
                label = "Tên nguồn",
                placeholder = "Ví dụ: Dân trí — Thế giới",
                enabled = !isBusy
            )
            SourceInput(
                value = state.rssUrl,
                onValueChange = onUrlChange,
                label = "RSS URL",
                placeholder = "https://example.com/rss.xml",
                enabled = !isBusy,
                keyboardType = KeyboardType.Uri
            )

            Button(
                onClick = onValidate,
                enabled = state.rssUrl.isNotBlank() && !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                if (state.isValidating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Đang kiểm tra…", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Kiểm tra RSS", fontWeight = FontWeight.SemiBold)
                }
            }

            state.errorMessage?.let { message ->
                SourceFeedback(
                    message = message,
                    color = StatusError,
                    icon = Icons.Filled.ErrorOutline
                )
            }
            if (state.validatedRssUrl == state.rssUrl.trim()) {
                SourceFeedback(
                    title = "Nguồn RSS hợp lệ",
                    message = listOfNotNull(state.detectedTitle, state.detectedSiteName)
                        .distinct()
                        .joinToString(" • ")
                        .ifBlank { "Đã nhận diện được nguồn RSS." },
                    color = StatusSuccess,
                    icon = Icons.Filled.CheckCircle
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Danh mục",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.categories, key = { it.id }) { category ->
                        FilterChip(
                            selected = category.id == state.category.id,
                            onClick = { onCategorySelected(category.id) },
                            enabled = !isBusy,
                            label = { Text(category.name) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isBusy,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text("Hủy", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onSave,
                    enabled = state.canSave,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = if (state.isSaving) "Đang lưu…" else "Thêm nguồn",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SurfaceCardSubtle,
            focusedContainerColor = SurfaceCardSubtle
        )
    )
}

@Composable
private fun SourceFeedback(
    message: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                title?.let {
                    Text(it, color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(message, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}
