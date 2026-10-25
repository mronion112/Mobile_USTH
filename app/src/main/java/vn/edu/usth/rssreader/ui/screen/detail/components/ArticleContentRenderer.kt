package vn.edu.usth.rssreader.ui.screen.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import vn.edu.usth.rssreader.ui.screen.detail.ArticleContentBlock
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

@Composable
fun ArticleContentRenderer(
    blocks: List<ArticleContentBlock>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is ArticleContentBlock.Paragraph -> {
                    if (block.isFirst) {
                        DropCapParagraph(text = block.text)
                    } else {
                        Text(
                            text = block.text,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                is ArticleContentBlock.Heading -> {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = block.text,
                        color = TextPrimary,
                        fontSize = if (block.level == 2) 20.sp else 18.sp,
                        lineHeight = if (block.level == 2) 28.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    )
                }
                is ArticleContentBlock.InlineImage -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceCardSubtle)
                        ) {
                            AsyncImage(
                                model = block.url,
                                contentDescription = block.caption,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (!block.caption.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = block.caption,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
                is ArticleContentBlock.Quote -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardSubtle)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "“${block.text}”",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
