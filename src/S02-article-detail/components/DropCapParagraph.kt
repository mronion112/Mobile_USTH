package vn.edu.usth.rssreader.ui.screen.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.TextPrimary

@Composable
fun DropCapParagraph(
    text: String,
    modifier: Modifier = Modifier
) {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return

    val firstChar = trimmed.take(1)
    val remaining = trimmed.drop(1)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Ký tự Drop-cap lớn màu xanh Teal
        Text(
            text = firstChar,
            color = Primary,
            fontSize = 46.sp,
            lineHeight = 46.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // Phần nội dung đoạn văn tiếp nối
        Text(
            text = remaining,
            color = TextPrimary,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
