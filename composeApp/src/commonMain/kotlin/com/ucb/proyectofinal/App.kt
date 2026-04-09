package com.ucb.proyectofinal

import androidx.compose.runtime.Composable
import com.ucb.proyectofinal.navigation.AppNavHost
import com.ucb.proyectofinal.remoteconfig.MaintenanceGate
import com.ucb.proyectofinal.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        MaintenanceGate {
            AppNavHost()
        }
    }
}
