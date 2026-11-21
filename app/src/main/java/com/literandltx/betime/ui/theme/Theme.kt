package com.literandltx.betime.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.literandltx.betime.data.preferences.AppThemeMode
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle

private val DiscordColors = darkColorScheme(
    primary = discord_primary,
    onPrimary = discord_onPrimary,
    primaryContainer = discord_primaryContainer,
    onPrimaryContainer = discord_onPrimaryContainer,
    secondary = discord_secondary,
    onSecondary = discord_onSecondary,
    background = discord_background,
    onBackground = discord_onBackground,
    surface = discord_surface,
    onSurface = discord_onSurface,
    surfaceVariant = discord_surfaceVariant,
    onSurfaceVariant = discord_onSurfaceVariant,
    outline = discord_outline,
    error = discord_error
)

@Composable
fun TimerTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.DISCORD -> true
        AppThemeMode.LIME -> isSystemInDarkTheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            setUpEdgeToEdge(view, darkTheme)
        }
    }

    when (themeMode) {
        AppThemeMode.DISCORD -> {
            MaterialTheme(
                colorScheme = DiscordColors,
                typography = Typography,
                content = content
            )
        }
        else -> {
            val seedColor = when (themeMode) {
                AppThemeMode.LIME -> SeedColorLime
                else -> SeedColor
            }

            DynamicMaterialTheme(
                seedColor = seedColor,
                isDark = darkTheme,
                style = PaletteStyle.Neutral,
                contrastLevel = 0.0,
                typography = Typography,
                content = content,
            )
        }
    }
}

/**
 * Sets up edge-to-edge for the window of this [view]. The system icon colors are set to either
 * light or dark depending on whether the [darkTheme] is enabled or not.
 */
private fun setUpEdgeToEdge(view: View, darkTheme: Boolean) {
    val window = (view.context as Activity).window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.Transparent.toArgb()
    val navigationBarColor = when {
        Build.VERSION.SDK_INT >= 29 -> Color.Transparent.toArgb()
        Build.VERSION.SDK_INT >= 26 -> Color(0xFF, 0xFF, 0xFF, 0x63).toArgb()
        // Min sdk version for this app is 24, this block is for SDK versions 24 and 25
        else -> Color(0x00, 0x00, 0x00, 0x50).toArgb()
    }
    window.navigationBarColor = navigationBarColor
    val controller = WindowCompat.getInsetsController(window, view)
    controller.isAppearanceLightStatusBars = !darkTheme
    controller.isAppearanceLightNavigationBars = !darkTheme
}
