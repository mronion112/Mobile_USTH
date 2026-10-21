package vn.edu.usth.rssreader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MobileUSTHColors = lightColorScheme(
    primary = PrimaryTeal,
    secondary = ActionTeal,
    background = Canvas,
    surface = White,
    onPrimary = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = StatusError,
)

@Composable
fun MobileUSTHTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MobileUSTHColors,
        typography = Typography(),
        content = content,
    )
}
