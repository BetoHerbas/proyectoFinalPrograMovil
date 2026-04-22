package com.ucb.proyectofinal.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.proyectofinal.ui.theme.AppColors

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // ── Top Bar ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.SurfaceVariant)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = AppColors.TextPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.weight(0.4f))
            Text("Configuración", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // ── CUENTA ───────────────────────────────────────────────────────
            SettingsSectionHeader("CUENTA")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup {
                SettingsRow(emoji = "👤", label = "Editar Perfil", onClick = {})
                SettingsDivider()
                SettingsRow(emoji = "🔑", label = "Cambiar Contraseña", onClick = {})
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── PREFERENCIAS ─────────────────────────────────────────────────
            SettingsSectionHeader("PREFERENCIAS")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup {
                // Notificaciones con toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔔", fontSize = 20.sp)
                        Column(modifier = Modifier.padding(start = 14.dp)) {
                            Text("Notificaciones", fontSize = 15.sp, color = AppColors.TextPrimary)
                            Text("Push, Email, SMS", fontSize = 12.sp, color = AppColors.TextSecondary)
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppColors.Background,
                            checkedTrackColor = AppColors.Primary,
                            uncheckedThumbColor = AppColors.TextSecondary,
                            uncheckedTrackColor = AppColors.SurfaceVariant
                        )
                    )
                }
                SettingsDivider()
                SettingsRow(emoji = "🔒", label = "Privacidad", onClick = {})
                SettingsDivider()
                SettingsRow(emoji = "📦", label = "Datos y Almacenamiento", onClick = {})
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── SOPORTE ──────────────────────────────────────────────────────
            SettingsSectionHeader("SOPORTE")
            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup {
                SettingsRow(emoji = "❓", label = "Ayuda y Soporte", onClick = {})
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Cerrar sesión ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.ErrorContainer)
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("↪", fontSize = 20.sp, color = AppColors.Error)
                    Spacer(modifier = Modifier.size(12.dp))
                    Text("Cerrar Sesión", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Error)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Versión
            Text(
                text = "Listify App v2.4.0\nMade with ❤️ for content lovers",
                fontSize = 11.sp,
                color = AppColors.TextDisabled,
                lineHeight = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary, letterSpacing = 1.5.sp)
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(emoji: String, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.size(14.dp))
            Text(label, fontSize = 15.sp, color = AppColors.TextPrimary)
        }
        Text("›", fontSize = 20.sp, color = AppColors.TextSecondary)
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 50.dp)
            .height(1.dp)
            .background(AppColors.SurfaceVariant)
    )
}
