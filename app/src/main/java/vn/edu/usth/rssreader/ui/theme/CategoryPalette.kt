package vn.edu.usth.rssreader.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import vn.edu.usth.rssreader.data.model.Category

data class CategoryColorScheme(
    val badgeBg: Color,
    val badgeContent: Color
)

val CategoryPalette: List<CategoryColorScheme> = listOf(
    /* 0 - blue   */ CategoryColorScheme(Color(0xFFDBEAFE), Color(0xFF1D4ED8)),
    /* 1 - orange */ CategoryColorScheme(Color(0xFFFFEDD5), Color(0xFFC2410C)),
    /* 2 - teal   */ CategoryColorScheme(Color(0xFFCCFBF1), Color(0xFF0F766E)),
    /* 3 - green  */ CategoryColorScheme(Color(0xFFDCFCE7), Color(0xFF15803D)),
    /* 4 - purple */ CategoryColorScheme(Color(0xFFEDE9FE), Color(0xFF6D28D9)),
    /* 5 - slate  */ CategoryColorScheme(Color(0xFFE2E8F0), Color(0xFF475569)),
    /* 6 - rose   */ CategoryColorScheme(Color(0xFFFFE4E6), Color(0xFFBE123C)),
    /* 7 - amber  */ CategoryColorScheme(Color(0xFFFEF3C7), Color(0xFFB45309)),
    /* 8 - cyan   */ CategoryColorScheme(Color(0xFFCFFAFE), Color(0xFF0E7490)),
    /* 9 - pink   */ CategoryColorScheme(Color(0xFFFCE7F3), Color(0xFF9D174D))
)

fun Category.scheme(): CategoryColorScheme =
    CategoryPalette[colorIndex.mod(CategoryPalette.size)]

private val CategoryIconMap: Map<String, ImageVector> = mapOf(
    "all"           to Icons.Filled.Newspaper,
    "the-gioi"      to Icons.Filled.Public,
    "kinh-doanh"    to Icons.Filled.TrendingUp,
    "cong-nghe"     to Icons.Filled.Memory,
    "the-thao"      to Icons.Filled.SportsSoccer,
    "giai-tri"      to Icons.Filled.TheaterComedy,
    "thoi-su"       to Icons.Filled.AccountBalance,
    "suc-khoe"      to Icons.Filled.HealthAndSafety,
    "giao-duc"      to Icons.Filled.School,
    "khoa-hoc"      to Icons.Filled.Science,
    "bat-dong-san"  to Icons.Filled.HomeWork
)

val DefaultCategoryIcon: ImageVector = Icons.Filled.Label

fun Category.icon(): ImageVector = CategoryIconMap[id] ?: DefaultCategoryIcon