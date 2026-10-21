package vn.edu.usth.rssreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.usth.rssreader.ui.theme.ActionTeal
import vn.edu.usth.rssreader.ui.theme.MobileUSTHTheme
import vn.edu.usth.rssreader.ui.theme.MutedSlate
import vn.edu.usth.rssreader.ui.theme.White

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileUSTHTheme {
                AppShell()
            }
        }
    }
}

private enum class Destination(
    @param:StringRes val label: Int,
    val icon: ImageVector,
) {
    Home(R.string.nav_home, Icons.Outlined.Home),
    Explore(R.string.nav_explore, Icons.Outlined.Explore),
    Saved(R.string.nav_saved, Icons.Outlined.BookmarkBorder),
    Rss(R.string.nav_rss, Icons.Outlined.RssFeed),
}

@Composable
private fun AppShell() {
    var selectedDestination by rememberSaveable { mutableStateOf(Destination.Home) }

    Scaffold(
        containerColor = White,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(64.dp),
                containerColor = White,
                tonalElevation = 8.dp,
            ) {
                Destination.entries.forEach { destination ->
                    val isSelected = destination == selectedDestination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = stringResource(destination.label),
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.label),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ActionTeal,
                            selectedTextColor = ActionTeal,
                            unselectedIconColor = MutedSlate,
                            unselectedTextColor = MutedSlate,
                            indicatorColor = Color.Transparent,
                        ),
                    )
                }
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(White),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppShellPreview() {
    MobileUSTHTheme {
        Surface {
            AppShell()
        }
    }
}
