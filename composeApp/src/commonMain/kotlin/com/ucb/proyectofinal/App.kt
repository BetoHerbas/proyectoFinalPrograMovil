package com.ucb.proyectofinal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.ucb.proyectofinal.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        AppNavHost()
    }
}
