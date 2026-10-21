package vn.edu.usth.rssreader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.ui.navigation.Destination
import vn.edu.usth.rssreader.ui.theme.Primary
import vn.edu.usth.rssreader.ui.theme.SurfaceCard
import vn.edu.usth.rssreader.ui.theme.TextMuted

@Composable
fun AppBottomNav(
    currentDestination: Destination,
    onNavigate: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceCard.copy(alpha = 0.9f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Destination.entries.forEach { destination ->
                BottomNavItem(
                    destination = destination,
                    selected = destination == currentDestination,
                    onClick = { onNavigate(destination) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    destination: Destination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) Primary else TextMuted
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = destination.label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = destination.label,
            color = tint,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            letterSpacing = 0.33.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FF, widthDp = 380)
@Composable
private fun PreviewAppBottomNav() {
    AppBottomNav(
        currentDestination = Destination.HOME,
        onNavigate = {}
    )
}