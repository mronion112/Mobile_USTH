package vn.edu.usth.rssreader.ui.screen.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import vn.edu.usth.rssreader.data.model.Article
import vn.edu.usth.rssreader.ui.components.article.CategoryBadge
import vn.edu.usth.rssreader.ui.screen.detail.components.ArticleContentRenderer
import vn.edu.usth.rssreader.ui.screen.detail.components.ArticleDetailTopBar
import vn.edu.usth.rssreader.ui.screen.detail.components.ArticleSummaryCard
import vn.edu.usth.rssreader.ui.theme.BorderSubtle
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.StatusError
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.SurfaceCardSubtle
import vn.edu.usth.rssreader.ui.theme.TextMuted
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary
import vn.edu.usth.rssreader.util.formatRelativeTime

private val SkyBlue = Color(0xFF0284C7)

@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleDetailViewModel = viewModel(
        key = articleId,
        factory = ArticleDetailViewModel.provideFactory(articleId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SurfaceCanvas,
        topBar = {
            ArticleDetailTopBar(
                title = "Chi tiết bài báo",
                isBookmarked = (uiState as? ArticleDetailUiState.Success)?.isBookmarked ?: false,
                onBackClick = onBackClick,
                onShareClick = {
                    (uiState as? ArticleDetailUiState.Success)?.let { success ->
                        shareArticle(context, success.article)
                    }
                },
                onBookmarkToggle = viewModel::toggleBookmark
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ArticleDetailUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Đang tải bài viết...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                is ArticleDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = state.message,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = viewModel::loadArticle,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Thử lại")
                        }
                    }
                }

                is ArticleDetailUiState.Success -> {
                    ArticleDetailContent(
                        article = state.article,
                        parsedData = state.parsedData,
                        isFallback = state.isFallback,
                        onOpenOriginal = {
                            openOriginalArticle(context, state.article.link)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleDetailContent(
    article: Article,
    parsedData: ParsedArticleData,
    isFallback: Boolean,
    onOpenOriginal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // 1. Chuyên mục + RSS Feed indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryBadge(category = article.category)

            Text("•", color = TextMuted, fontSize = 12.sp)

            Text(
                text = "RSS Feed",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(12.dp))

        // 2. Tiêu đề bài viết (Plus Jakarta Sans 26sp/800)
        Text(
            text = parsedData.title,
            color = TextPrimary,
            fontSize = 26.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.65).sp
        )

        Spacer(Modifier.height(12.dp))

        // 3. Tác giả & Ngày đăng (Byline)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar đại diện nguồn tin
            val sourceInitials = article.source.take(2).uppercase()
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sourceInitials,
                    color = Primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val authorText = parsedData.author ?: article.author ?: article.source
            Text(
                text = authorText,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text("•", color = TextMuted, fontSize = 12.sp)

            val displayTime = parsedData.publishedTime ?: formatRelativeTime(article.publishedAt)
            Text(
                text = displayTime,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        // 4. Hero Image (16:9, 14dp radius)
        val heroUrl = parsedData.heroImageUrl ?: article.imageUrl
        if (heroUrl.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCardSubtle)
            ) {
                AsyncImage(
                    model = heroUrl,
                    contentDescription = parsedData.heroCaption ?: parsedData.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Caption overlay nếu có
                if (!parsedData.heroCaption.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )
                    Text(
                        text = parsedData.heroCaption,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // 5. Thẻ tóm tắt phát lệnh RSS (Sapo Callout Card)
        val summaryText = parsedData.sapo ?: article.description
        if (!summaryText.isNullOrBlank()) {
            ArticleSummaryCard(summaryText = summaryText)
            Spacer(Modifier.height(20.dp))
        }

        // 6. Thông báo Fallback khi không cào được nội dung toàn văn
        if (isFallback) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardSubtle)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = SkyBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Đang hiển thị tóm tắt từ nguồn RSS. Nhấn nút bên dưới để đọc toàn văn trên báo gốc.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // 7. Nội dung bài viết (Đoạn văn có Drop-cap, ảnh inline kèm chú thích)
        if (parsedData.blocks.isNotEmpty()) {
            ArticleContentRenderer(blocks = parsedData.blocks)
            Spacer(Modifier.height(28.dp))
        }

        // 8. Nút "Đọc bài gốc trên Dân trí ↗" (Sky-blue treatment theo DESIGN2.md)
        if (article.link.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SkyBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenOriginal)
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Đọc bài gốc trên ${article.source}",
                        color = SkyBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.height(36.dp))
        }
    }
}

private fun shareArticle(context: Context, article: Article) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, article.title)
        val shareBody = "${article.title}\n${article.link}"
        putExtra(Intent.EXTRA_TEXT, shareBody)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết"))
}

private fun openOriginalArticle(context: Context, url: String) {
    val trimmed = url.trim()
    if (trimmed.isBlank() || !trimmed.startsWith("http")) {
        Toast.makeText(context, "Liên kết bài báo không hợp lệ", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmed))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Không thể mở trình duyệt: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
