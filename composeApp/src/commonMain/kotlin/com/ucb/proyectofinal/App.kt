package com.ucb.proyectofinal

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.ucb.proyectofinal.designsystem.theme.DsTheme
import com.ucb.proyectofinal.designsystem.theme.ThemeMode
import com.ucb.proyectofinal.navigation.AppNavHost
import com.ucb.proyectofinal.notification.getToken
import com.ucb.proyectofinal.remoteconfig.MaintenanceGate

@Composable
fun App() {
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        try {
            val token = getToken()
            println("FIREBASE_TOKEN: $token")
        } catch (e: Exception) {
            println("FIREBASE_TOKEN Error: ${e.message}")
        }
    }

    DsTheme(
        mode = if (isDark) ThemeMode.DARK else ThemeMode.LIGHT
    ) {
        MaintenanceGate {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { _ ->
                AppNavHost()
            }
        }
    }
}
