package com.ucb.proyectofinal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.ucb.proyectofinal.navigation.AppNavHost
import com.ucb.proyectofinal.remoteconfig.MaintenanceGate

import androidx.compose.runtime.LaunchedEffect
import com.ucb.proyectofinal.notification.getToken

@Composable
fun App() {
    LaunchedEffect(Unit) {
        try {
            val token = getToken()
            println("FIREBASE_TOKEN: $token")
        } catch (e: Exception) {
            println("FIREBASE_TOKEN Error: ${e.message}")
        }
    }

    MaterialTheme {
        // Verifica Remote Config antes de mostrar cualquier pantalla.
        // Si "mantainence" == true → muestra MaintenanceScreen.
        // Si false (o error) → muestra la app normal.
        MaintenanceGate {
            AppNavHost()
        }
    }
}
