package vn.edu.usth.rssreader.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import vn.edu.usth.rssreader.ui.components.AppBottomNav
import vn.edu.usth.rssreader.ui.components.AppTopBar
import vn.edu.usth.rssreader.ui.theme.SurfaceCanvas

@Composable
fun AppScaffold(
    currentDestination: Destination,
    onNavigate: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = SurfaceCanvas,
//        topBar = {
//            AppTopBar(
//                title = currentDestination.title,
//                onSearchClick = onSearchClick,
//                onAvatarClick = onAvatarClick
//            )
//        },
        bottomBar = {
            AppBottomNav(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        },
        content = content
    )
}