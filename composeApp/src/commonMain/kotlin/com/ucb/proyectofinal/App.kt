package com.ucb.proyectofinal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.ucb.proyectofinal.navigation.AppNavHost
import com.ucb.proyectofinal.remoteconfig.MaintenanceGate

@Composable
fun App() {
    MaterialTheme {
        // Verifica Remote Config antes de mostrar cualquier pantalla.
        // Si "mantainence" == true → muestra MaintenanceScreen.
        // Si false (o error) → muestra la app normal.
        MaintenanceGate {
            AppNavHost()
        }
    }
}
