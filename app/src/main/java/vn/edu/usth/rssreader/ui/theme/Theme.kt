package vn.edu.usth.rssreader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary              = Primary,
    onPrimary            = OnPrimary,
    primaryContainer     = PrimaryContainer,
    onPrimaryContainer   = OnPrimary,

    background           = Background,
    onBackground         = TextPrimary,

    surface              = SurfaceCard,
    onSurface            = TextPrimary,
    surfaceVariant       = SurfaceCardSubtle,
    onSurfaceVariant     = TextSecondary,

    outline              = BorderSubtle,
    outlineVariant       = BorderSubtle,

    error                = StatusError,
    onError              = OnPrimary,
)

@Composable
fun MobileUSTHTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}