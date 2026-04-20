package com.ucb.proyectofinal.designsystem.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ucb.proyectofinal.designsystem.theme.AppTheme

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = AppTheme.colors.textPrimary.copy(alpha = 0.1f)
    )
}
