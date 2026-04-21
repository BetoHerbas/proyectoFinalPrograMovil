package com.ucb.proyectofinal.designsystem.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ucb.proyectofinal.designsystem.theme.AppTheme

@Composable
fun BasicInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        singleLine = singleLine,
        isError = isError,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        supportingText = supportingText?.let { { Text(it) } },
        textStyle = AppTheme.typography.bodyMedium.copy(
            color = AppTheme.colors.textPrimary
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AppTheme.colors.textPrimary,
            unfocusedTextColor = AppTheme.colors.textPrimary,
            disabledTextColor = AppTheme.colors.textPrimary.copy(alpha = 0.38f),
            errorTextColor = AppTheme.colors.error,
            
            focusedBorderColor = AppTheme.colors.primary,
            unfocusedBorderColor = AppTheme.colors.textPrimary.copy(alpha = 0.5f),
            disabledBorderColor = AppTheme.colors.textPrimary.copy(alpha = 0.12f),
            errorBorderColor = AppTheme.colors.error,
            
            focusedLabelColor = AppTheme.colors.primary,
            unfocusedLabelColor = AppTheme.colors.textPrimary.copy(alpha = 0.6f),
            errorLabelColor = AppTheme.colors.error,
            
            cursorColor = AppTheme.colors.primary,
            selectionColors = TextSelectionColors(
                handleColor = AppTheme.colors.primary,
                backgroundColor = AppTheme.colors.primary.copy(alpha = 0.4f)
            )
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
