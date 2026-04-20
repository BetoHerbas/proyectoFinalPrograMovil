package com.ucb.proyectofinal.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val error: Color,
    val isLight: Boolean
)

val LightPalette = AppColors(
    primary = Color(0xFF6200EE),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121212),
    textPrimary = Color(0xFF212121),
    textSecondary = Color(0xFF757575),
    error = Color(0xFFB00020),
    isLight = true
)

val DarkPalette = AppColors(
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFB0B0B0),
    error = Color(0xFFCF6679),
    isLight = false
)

val HighContrastPalette = AppColors(
    primary = Color(0xFFFFFF00),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF00FFFF),
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFFFFF00),
    error = Color(0xFFFF0000),
    isLight = false
)
