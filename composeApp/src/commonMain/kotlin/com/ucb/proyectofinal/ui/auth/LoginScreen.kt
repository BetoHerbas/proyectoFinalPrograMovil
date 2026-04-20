package com.ucb.proyectofinal.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.proyectofinal.designsystem.components.AppCard
import com.ucb.proyectofinal.designsystem.components.BasicInput
import com.ucb.proyectofinal.designsystem.components.PrimaryButton
import com.ucb.proyectofinal.designsystem.components.SecondaryButton
import com.ucb.proyectofinal.designsystem.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import proyectofinalprogramovil.composeapp.generated.resources.Res
import proyectofinalprogramovil.composeapp.generated.resources.login_screen_title

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(AppTheme.spacing.medium)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la pantalla
        Text(
            text = stringResource(Res.string.login_screen_title),
            style = AppTheme.typography.headlineLarge,
            color = AppTheme.colors.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.large))

        // Tarjeta del formulario
        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(AppTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
            ) {
                Text(
                    text = "Bienvenido de nuevo",
                    style = AppTheme.typography.headlineSmall,
                    color = AppTheme.colors.textPrimary
                )

                Text(
                    text = "Ingresa tus credenciales para continuar",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.small))

                BasicInput(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    modifier = Modifier.fillMaxWidth()
                )

                BasicInput(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    modifier = Modifier.fillMaxWidth()
                    // Aquí se podría añadir visualTransformation para contraseñas
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.medium))

                PrimaryButton(
                    text = "Iniciar Sesión",
                    onClick = { 
                        // Simulación visual de carga
                        isLoading = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = isLoading
                )

                SecondaryButton(
                    text = "Crear una cuenta",
                    onClick = { /* Navegar a registro */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.extraLarge))
        
        // Footer informativo para ver el comportamiento del color secundario
        Text(
            text = "© 2026 Proyecto Final Moviles",
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.textSecondary
        )
    }
}
