package com.ucb.proyectofinal.profile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.proyectofinal.profile.presentation.effect.ProfileEffect
import com.ucb.proyectofinal.profile.presentation.intent.ProfileIntent
import com.ucb.proyectofinal.profile.presentation.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProfileEffect.NavigateToLogin -> onLogout()
                is ProfileEffect.ShowError -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101715))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text("Perfil", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(shape = CircleShape, color = Color(0xFF00E5B6), modifier = Modifier.fillMaxSize()) {}
            Text(
                text = state.user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color(0xFF101715),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.user?.name ?: "Usuario",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = state.user?.email?.value ?: "",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { viewModel.onIntent(ProfileIntent.Logout) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            border = ButtonDefaults.outlinedButtonBorder(true).copy(
                width = 1.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5B6))
        ) {
            Text("Cerrar sesión", fontWeight = FontWeight.SemiBold)
        }
    }
}
