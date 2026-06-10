package com.ucb.proyectofinal.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    LIGHT,
    DARK,
    HIGH_CONTRAST
}

private val LocalColors = staticCompositionLocalOf { LightPalette }
private val LocalTypography = staticCompositionLocalOf { DefaultTypography }

private val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF22F2D4),
    onPrimary = Color(0xFF003730),
    primaryContainer = Color(0xFF004D46),
    onPrimaryContainer = Color(0xFFB7FBEF),
    secondary = Color(0xFF4DC9B8),
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF1A4A44),
    onSecondaryContainer = Color(0xFF9DF0E0),
    background = Color(0xFF0A1D26),
    onBackground = Color(0xFFE8FAFF),
    surface = Color(0xFF0B2535),
    onSurface = Color(0xFFE8FAFF),
    surfaceVariant = Color(0xFF163545),
    onSurfaceVariant = Color(0xFFB0CAD5),
    outline = Color(0xFF3A6470),
    outlineVariant = Color(0xFF1E3D4D),
    error = Color(0xFFFF8080),
    onError = Color(0xFF690005),
)

private val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF006B5E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9EF2E0),
    onPrimaryContainer = Color(0xFF00201B),
    secondary = Color(0xFF4A6360),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8E4),
    onSecondaryContainer = Color(0xFF051F1D),
    background = Color(0xFFF2F9F8),
    onBackground = Color(0xFF191C1B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C1B),
    surfaceVariant = Color(0xFFDAE5E1),
    onSurfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF6F7975),
    outlineVariant = Color(0xFFBEC9C5),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
        
    val spacing: AppSpacing
        get() = AppSpacing
}

@Composable
fun DsTheme(
    mode: ThemeMode = if (isSystemInDarkTheme()) ThemeMode.DARK else ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val appColors = when (mode) {
        ThemeMode.LIGHT -> LightPalette
        ThemeMode.DARK -> DarkPalette
        ThemeMode.HIGH_CONTRAST -> HighContrastPalette
    }
    val m3ColorScheme = when (mode) {
        ThemeMode.LIGHT -> AppLightColorScheme
        ThemeMode.DARK, ThemeMode.HIGH_CONTRAST -> AppDarkColorScheme
    }

    MaterialTheme(colorScheme = m3ColorScheme) {
        CompositionLocalProvider(
            LocalColors provides appColors,
            LocalTypography provides DefaultTypography
        ) {
            content()
        }
    }
}
