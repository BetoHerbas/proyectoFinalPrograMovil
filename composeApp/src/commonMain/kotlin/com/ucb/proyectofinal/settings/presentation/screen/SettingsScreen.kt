package com.ucb.proyectofinal.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.ucb.proyectofinal.settings.presentation.intent.SettingsIntent
import com.ucb.proyectofinal.settings.presentation.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101715))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text("Ajustes", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Dark mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modo oscuro", color = Color.White)
            Switch(
                checked = state.isDarkMode,
                onCheckedChange = { viewModel.onIntent(SettingsIntent.ToggleDarkMode(it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF101715), checkedTrackColor = Color(0xFF00E5B6))
            )
        }

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 16.dp))

        // Language selection
        Text("Idioma", color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("es" to "Español", "en" to "English").forEach { (code, label) ->
                FilterChip(
                    selected = state.language == code,
                    onClick = { viewModel.onIntent(SettingsIntent.ChangeLanguage(code)) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00E5B6),
                        selectedLabelColor = Color(0xFF101715),
                        containerColor = Color(0xFF1A2421),
                        labelColor = Color.White
                    )
                )
            }
        }
    }
}
