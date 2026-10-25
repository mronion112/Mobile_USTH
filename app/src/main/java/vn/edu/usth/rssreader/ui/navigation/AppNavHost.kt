package vn.edu.usth.rssreader.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import vn.edu.usth.rssreader.ui.screen.detail.ArticleDetailScreen
import vn.edu.usth.rssreader.ui.screen.explore.ExploreScreen
import vn.edu.usth.rssreader.ui.screen.home.HomeScreen
import vn.edu.usth.rssreader.ui.screen.rss.RssScreen
import vn.edu.usth.rssreader.ui.screen.saved.SavedScreen

private const val TAB_ANIM_MS = 300
private const val ARTICLE_ROUTE = "article/{articleId}"

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
    val currentRoute = backStackEntry?.destination?.route
    val isArticleDetail = currentRoute?.startsWith("article/") == true
    val currentDestination = if (isArticleDetail) null else Destination.fromRoute(currentRoute)

    var focusExploreSearch by remember { mutableStateOf(false) }

    val navigateToTab: (Destination) -> Unit = { destination ->
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToArticle: (String) -> Unit = { articleId ->
        navController.navigate("article/$articleId")
    }

    AppScaffold(
        currentDestination = currentDestination,
        onNavigate = navigateToTab,
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier.padding(if (isArticleDetail) PaddingValues() else innerPadding),
            enterTransition = { tabEnter() },
            exitTransition = { tabExit() },
            popEnterTransition = { tabEnter() },
            popExitTransition = { tabExit() }
        ) {
            composable(Destination.HOME.route) {
                HomeScreen(
                    onSearchClick = {
                        focusExploreSearch = true
                        navigateToTab(Destination.EXPLORE)
                    },
                    onArticleClick = navigateToArticle
                )
            }
            composable(Destination.EXPLORE.route) {
                ExploreScreen(
                    focusSearchOnEnter = focusExploreSearch,
                    onSearchFocused = { focusExploreSearch = false },
                    onArticleClick = navigateToArticle
                )
            }
            composable(Destination.SAVED.route) {
                SavedScreen(onArticleClick = navigateToArticle)
            }
            composable(Destination.RSS.route) {
                RssScreen()
            }
            composable(
                route = ARTICLE_ROUTE,
                arguments = listOf(navArgument("articleId") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(tween(TAB_ANIM_MS)) { it } + fadeIn(tween(TAB_ANIM_MS))
                },
                exitTransition = {
                    slideOutHorizontally(tween(TAB_ANIM_MS)) { -it } + fadeOut(tween(TAB_ANIM_MS))
                }
            ) { entry ->
                ArticleDetailScreen(
                    articleId = entry.arguments?.getString("articleId").orEmpty(),
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
