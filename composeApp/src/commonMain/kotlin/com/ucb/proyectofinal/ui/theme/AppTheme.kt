package com.ucb.proyectofinal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = AppColors.Primary,
    onPrimary        = AppColors.Background,
    primaryContainer = AppColors.PrimaryContainer,
    background       = AppColors.Background,
    surface          = AppColors.Surface,
    surfaceVariant   = AppColors.SurfaceVariant,
    onBackground     = AppColors.TextPrimary,
    onSurface        = AppColors.TextPrimary,
    onSurfaceVariant = AppColors.TextSecondary,
    error            = AppColors.Error,
    errorContainer   = AppColors.ErrorContainer,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content     = content
    )
}
