package vn.edu.usth.rssreader.ui.screen.rss.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

/** S08: confirmation is tied to the id/name snapshot captured from S06. */
@Composable
fun RemoveSourceConfirmationDialog(
    sourceName: String,
    isRemoving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (!isRemoving) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isRemoving,
            dismissOnClickOutside = !isRemoving
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceCard,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Xóa nguồn RSS?",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sourceName,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Nguồn này sẽ ngừng được tải mới. Các bài viết và mục đã lưu hiện có vẫn được giữ lại.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = StatusError,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        enabled = !isRemoving,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceCardSubtle,
                            contentColor = TextSecondary,
                            disabledContainerColor = SurfaceCardSubtle,
                            disabledContentColor = TextSecondary.copy(alpha = 0.55f)
                        )
                    ) {
                        Text(
                            text = "Hủy",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onConfirm,
                        enabled = !isRemoving,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusError,
                            contentColor = SurfaceCard,
                            disabledContainerColor = StatusError.copy(alpha = 0.72f),
                            disabledContentColor = SurfaceCard
                        )
                    ) {
                        if (isRemoving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                color = SurfaceCard,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Xóa nguồn",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
