package vn.edu.usth.rssreader.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val title: String,
    val icon: ImageVector
) {
    HOME(
        route = "home",
        label = "Trang chủ",
        title = "Trang Chủ",
        icon = Icons.Filled.Home
    ),
    EXPLORE(
        route = "explore",
        label = "Khám phá",
        title = "Khám Phá",
        icon = Icons.Filled.Explore
    ),
    SAVED(
        route = "saved",
        label = "Đã lưu",
        title = "Đã Lưu",
        icon = Icons.Filled.Bookmark
    ),
    RSS(
        route = "rss",
        label = "Nguồn RSS",
        title = "Nguồn RSS",
        icon = Icons.Filled.RssFeed
    );

    companion object {
        fun fromRoute(route: String?): Destination =
            entries.firstOrNull { it.route == route } ?: HOME
    }
}