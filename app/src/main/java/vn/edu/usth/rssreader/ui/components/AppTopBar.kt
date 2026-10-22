package vn.edu.usth.rssreader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import vn.edu.usth.rssreader.ui.theme.OnPrimary
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextPrimary
import vn.edu.usth.rssreader.ui.theme.TextSecondary

private const val LOGO_URL =
    "https://lh3.googleusercontent.com/aida/AEtjO1VBXACMdNAJ3yUnd49lLGhhfn307n0YqW223H76wBmj4fpygTh5NFfuJdHYdCLwOF9PAOfcGBs84cbhYI1BfG-BHEUcgHXBMj0ucbrq2nVVUvkNsD2TVxlx_d5110UD3HheisBpZoOb22fmQ05cg4rbMrOifrrswBhiNwF3BUt--FvTjzd0JjXD1cGPkdPo0NfqPGSyXLEu1VItKidAlKQfqRsjV40a71de5SrJwGWmU7BROfJUy30CvMc"

@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceCard.copy(alpha = 0.85f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ─── Left: logo + title ─────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = LOGO_URL,
                    contentDescription = "Dân trí RSS",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.16).sp
                )
            }

            // ─── Right: search + avatar ─────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CircleIconButton(
                    icon = Icons.Filled.Search,
                    contentDescription = "Tìm kiếm nhanh",
                    onClick = onSearchClick,
                    size = 44.dp,
                    iconSize = 22.dp,
                    iconTint = TextSecondary
                )
                AvatarButton(onClick = onAvatarClick)
            }
        }
    }
}

@Composable
private fun AvatarButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Tài khoản",
            tint = OnPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FF, widthDp = 380)
@Composable
private fun PreviewAppTopBar() {
    AppTopBar(title = "Trang Chủ")
}