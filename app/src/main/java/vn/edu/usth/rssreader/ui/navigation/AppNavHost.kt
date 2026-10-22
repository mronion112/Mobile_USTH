package vn.edu.usth.rssreader.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import vn.edu.usth.rssreader.ui.screen.explore.ExploreScreen
import vn.edu.usth.rssreader.ui.screen.home.HomeScreen
import vn.edu.usth.rssreader.ui.screen.rss.RssScreen
import vn.edu.usth.rssreader.ui.screen.saved.SavedScreen

private const val TAB_ANIM_MS = 300

private fun tabIndex(route: String?): Int =
    Destination.entries.indexOfFirst { it.route == route }.takeIf { it >= 0 } ?: 0

private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabEnter(): EnterTransition {
    val forward = tabIndex(targetState.destination.route) >= tabIndex(initialState.destination.route)
    val offset: (Int) -> Int = if (forward) ({ it }) else ({ -it })
    return slideInHorizontally(tween(TAB_ANIM_MS), offset) + fadeIn(tween(TAB_ANIM_MS))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabExit(): ExitTransition {
    val forward = tabIndex(targetState.destination.route) >= tabIndex(initialState.destination.route)
    val offset: (Int) -> Int = if (forward) ({ -it }) else ({ it })
    return slideOutHorizontally(tween(TAB_ANIM_MS), offset) + fadeOut(tween(TAB_ANIM_MS))
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = Destination.fromRoute(
        backStackEntry?.destination?.route
    )

    AppScaffold(
        currentDestination = currentDestination,
        onNavigate = { destination ->
            navController.navigate(destination.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { tabEnter() },
            exitTransition = { tabExit() },
            popEnterTransition = { tabEnter() },
            popExitTransition = { tabExit() }
        ) {
            composable(Destination.HOME.route) {
                HomeScreen()
            }
            composable(Destination.EXPLORE.route) {
                ExploreScreen()
            }
            composable(Destination.SAVED.route) {
                SavedScreen()
            }
            composable(Destination.RSS.route) {
                RssScreen()
            }
        }
    }
}