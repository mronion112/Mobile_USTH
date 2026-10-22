package vn.edu.usth.rssreader.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            modifier = Modifier.padding(innerPadding)
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