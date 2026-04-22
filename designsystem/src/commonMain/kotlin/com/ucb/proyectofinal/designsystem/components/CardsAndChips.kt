package com.ucb.proyectofinal.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ucb.proyectofinal.designsystem.theme.AppTheme

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) AppTheme.colors.primary else Color.Transparent
    val contentColor = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.textSecondary
    val borderColor = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textSecondary.copy(alpha = 0.5f)

    Surface(
        modifier = modifier.clickable { onClick() },
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = text,
                style = AppTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.colors.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
        border = BorderStroke(0.5.dp, AppTheme.colors.textPrimary.copy(alpha = 0.1f))
    ) {
        content()
    }
}
