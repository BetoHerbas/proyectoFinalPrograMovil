package com.ucb.proyectofinal.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import proyectofinalprogramovil.composeapp.generated.resources.Res
import proyectofinalprogramovil.composeapp.generated.resources.login_screen_title

@Composable
fun LoginScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(Res.string.login_screen_title))
    }
}
