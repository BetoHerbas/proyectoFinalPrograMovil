package com.ucb.proyectofinal

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.ucb.proyectofinal.designsystem.theme.DsTheme
import com.ucb.proyectofinal.designsystem.theme.ThemeMode
import com.ucb.proyectofinal.navigation.AppNavHost
import com.ucb.proyectofinal.notification.data.datasource.getToken
import com.ucb.proyectofinal.maintenance.presentation.composable.MaintenanceGate
import org.jetbrains.compose.resources.stringResource
import proyectofinalprogramovil.composeapp.generated.resources.Res
import proyectofinalprogramovil.composeapp.generated.resources.maintenance_finished
import com.ucb.proyectofinal.settings.data.datasource.AppSettingsStore

@Composable
fun App() {
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkMode by AppSettingsStore.isDarkMode.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val token = getToken()
            println("FIREBASE_TOKEN: $token")
        } catch (e: Exception) {
            println("FIREBASE_TOKEN Error: ${e.message}")
        }
    }

    val maintenanceFinishedMessage = stringResource(Res.string.maintenance_finished)

    DsTheme(
        mode = if (isDarkMode) ThemeMode.DARK else ThemeMode.LIGHT
    ) {
        MaintenanceGate(
            onMaintenanceFinished = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(maintenanceFinishedMessage)
                }
            }
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { _ ->
                AppNavHost()
            }
        }
    }
}
