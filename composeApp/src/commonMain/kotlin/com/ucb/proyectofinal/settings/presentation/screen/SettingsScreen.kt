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
import com.ucb.proyectofinal.navigation.AppBottomBar
import com.ucb.proyectofinal.navigation.BottomTab
import com.ucb.proyectofinal.settings.presentation.state.SettingsIntent
import com.ucb.proyectofinal.settings.presentation.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import proyectofinalprogramovil.composeapp.generated.resources.Res
import proyectofinalprogramovil.composeapp.generated.resources.common_back
import proyectofinalprogramovil.composeapp.generated.resources.settings_title
import proyectofinalprogramovil.composeapp.generated.resources.settings_dark_mode
import proyectofinalprogramovil.composeapp.generated.resources.settings_language

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                currentTab = BottomTab.SETTINGS,
                onNavigateToHome = onNavigateToHome,
                onNavigateToExplore = onNavigateToExplore,
                onNavigateToCreate = onNavigateToCreate,
                onNavigateToFavorites = onNavigateToFavorites,
                onNavigateToSettings = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.common_back), tint = MaterialTheme.colorScheme.onBackground)
                }
                Text(stringResource(Res.string.settings_title), color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Dark mode toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.settings_dark_mode), color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = state.isDarkMode,
                    onCheckedChange = { viewModel.onIntent(SettingsIntent.ToggleDarkMode(it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.background,
                        checkedTrackColor = Color(0xFF00E5B6),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 16.dp))

            // Language selection
            Text(stringResource(Res.string.settings_language), color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("es" to "Español", "en" to "English", "fr" to "Français").forEach { (code, label) ->
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
}
