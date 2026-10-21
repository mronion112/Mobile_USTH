package vn.edu.usth.rssreader.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object EditorialPulseColors {
    val SurfaceCanvas = Color(0xFFF8FAFC)
    val SurfaceCard = Color(0xFFFFFFFF)
    val SurfaceCardSubtle = Color(0xFFF1F5F9)
    val PrimaryM3 = Color(0xFF005C55)
    val PrimaryTeal = Color(0xFF0F766E)
    val PrimaryTealSoft = Color(0xFFCCFBF1)
    val PrimaryTealSurface = Color(0xFFF0FDFA)
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextMuted = Color(0xFF94A3B8)
    val BorderSubtle = Color(0xFFE2E8F0)
    val BorderFocus = Color(0xFF0F766E)
    val SecondaryAmber = Color(0xFFD97706)
    val SecondaryAmberSoft = Color(0xFFFEF3C7)
    val TertiarySky = Color(0xFF0284C7)
    val StatusActive = Color(0xFF16A34A)
    val StatusActiveSoft = Color(0xFFDCFCE7)
    val StatusError = Color(0xFFDC2626)
    val StatusErrorSoft = Color(0xFFFEF2F2)
    val StatusErrorBorder = Color(0xFFFECACA)
}

private val EditorialLightColorScheme = lightColorScheme(
    primary = EditorialPulseColors.PrimaryTeal,
    onPrimary = EditorialPulseColors.SurfaceCard,
    secondary = EditorialPulseColors.SecondaryAmber,
    tertiary = EditorialPulseColors.TertiarySky,
    background = EditorialPulseColors.SurfaceCanvas,
    surface = EditorialPulseColors.SurfaceCard,
    onSurface = EditorialPulseColors.TextPrimary,
    onSurfaceVariant = EditorialPulseColors.TextSecondary,
    outline = EditorialPulseColors.BorderSubtle,
    error = EditorialPulseColors.StatusError,
)

@Composable
fun EditorialPulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EditorialLightColorScheme,
        typography = Typography(),
        content = content,
    )
}
