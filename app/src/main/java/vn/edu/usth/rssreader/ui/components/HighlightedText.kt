package vn.edu.usth.rssreader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import vn.edu.usth.rssreader.ui.theme.PrimaryFixed
import vn.edu.usth.rssreader.ui.theme.TextPrimary

@Composable
fun HighlightedText(
    text: String,
    keyword: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        color = TextPrimary,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.16).sp
    ),
    highlightBackground: Color = PrimaryFixed,
    maxLines: Int = 2,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val annotated: AnnotatedString = buildAnnotatedString {
        if (keyword.isBlank()) {
            append(text)
            return@buildAnnotatedString
        }

        val lowerText = text.lowercase()
        val lowerKeyword = keyword.lowercase()
        var startIndex = 0

        while (true) {
            val found = lowerText.indexOf(lowerKeyword, startIndex)
            if (found < 0) {
                append(text.substring(startIndex))
                break
            }

            if (found > startIndex) {
                append(text.substring(startIndex, found))
            }

            withStyle(
                SpanStyle(
                    background = highlightBackground,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(found, found + keyword.length))
            }
            startIndex = found + keyword.length
        }
    }

    Text(
        text = annotated,
        modifier = modifier,
        style = textStyle,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 340)
@Composable
private fun PreviewHighlightedText() {
    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
        HighlightedText(
            text = "Ứng dụng Trí tuệ nhân tạo giúp nâng cao năng suất kỹ sư phần mềm Việt Nam",
            keyword = "Trí tuệ nhân tạo"
        )
        androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))
        HighlightedText(
            text = "Làn sóng đầu tư vào chip bán dẫn và hệ sinh thái trí tuệ nhân tạo tại Đà Nẵng",
            keyword = "trí tuệ nhân tạo"
        )
        androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))
        HighlightedText(
            text = "Không có keyword trong bài này",
            keyword = "blockchain"
        )
    }
}