package com.ucb.proyectofinal.ui.settings

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.proyectofinal.core.data.db.RemoteConfigEntity
import com.ucb.proyectofinal.remoteconfig.RemoteConfigUiState
import com.ucb.proyectofinal.remoteconfig.RemoteConfigViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla de Ajustes que muestra la configuración remota cacheada localmente.
 *
 * Lee datos de Room (offline-first):
 * - Si hay datos cacheados → muestra las claves/valores con timestamp
 * - Si no hay datos → muestra mensaje invitando a conectar a internet
 * - Los datos se actualizan reactivamente cuando el Worker sincroniza
 */
@Composable
fun SettingsScreen(
    viewModel: RemoteConfigViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0F1A), Color(0xFF1A1A2E))
                )
            )
    ) {
        // ── Header ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "⚙️",
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Configuración Remota",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Datos sincronizados desde Firebase Remote Config",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.08f),
            thickness = 1.dp
        )

        // ── Content ────────────────────────────────────────────
        when (val currentState = state) {
            is RemoteConfigUiState.Loading -> LoadingContent()
            is RemoteConfigUiState.Empty -> EmptyContent()
            is RemoteConfigUiState.Error -> ErrorContent(currentState.message)
            is RemoteConfigUiState.Loaded -> LoadedContent(currentState.configs)
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Sub-composables para cada estado
// ─────────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale),
                color = Color(0xFF6C63FF),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando configuración…",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0xFF6C63FF).copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("📡", fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Sin datos cacheados",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Conecta a internet para sincronizar\nla configuración remota por primera vez.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0xFFFF6B6B).copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("⚠️", fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Error al leer la caché",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadedContent(configs: List<RemoteConfigEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Badge de estado ──────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF4CAF50), shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Caché local activa · ${configs.size} parámetros",
                    fontSize = 13.sp,
                    color = Color(0xFF4CAF50).copy(alpha = 0.9f)
                )
            }
        }

        // ── Cards de configuración ───────────────────────────
        items(configs) { config ->
            ConfigCard(config)
        }

        // ── Footer informativo ───────────────────────────────
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "💡 Los datos se descargan al iniciar la app y se " +
                        "guardan localmente. Sin internet, se usa la última versión.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.35f),
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConfigCard(config: RemoteConfigEntity) {
    val labelForKey = when (config.key) {
        "mantainence" -> "🔧  Modo Mantenimiento"
        "welcome_message" -> "👋  Mensaje de Bienvenida"
        "app_min_version" -> "📱  Versión Mínima"
        else -> "🔑  ${config.key}"
    }

    val displayValue = when (config.key) {
        "mantainence" -> if (config.value == "true" || config.value == "1") "Activado" else "Desactivado"
        else -> config.value.ifBlank { "(vacío)" }
    }

    val accentColor = when (config.key) {
        "mantainence" -> if (config.value == "true" || config.value == "1") Color(0xFFFF6B6B) else Color(0xFF4CAF50)
        "welcome_message" -> Color(0xFF6C63FF)
        "app_min_version" -> Color(0xFF00BCD4)
        else -> Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E32)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Clave
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = labelForKey,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Valor
            Box(
                modifier = Modifier
                    .background(
                        color = accentColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = displayValue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp
            if (config.updatedAt > 0) {
                Text(
                    text = "Última sync: ${formatTimestamp(config.updatedAt)}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.3f)
                )
            }

            // Clave técnica
            Text(
                text = "key: ${config.key}",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

/**
 * Formatea un timestamp epoch-millis a una representación legible.
 */
private fun formatTimestamp(epochMillis: Long): String {
    // Formato simple sin dependencias adicionales
    val seconds = epochMillis / 1000
    val minutes = (seconds / 60) % 60
    val hours = (seconds / 3600) % 24
    val days = seconds / 86400

    // Calcular fecha aproximada desde epoch
    val totalDays = (epochMillis / 86_400_000).toInt()
    var year = 1970
    var remainingDays = totalDays

    while (true) {
        val daysInYear = if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 366 else 365
        if (remainingDays < daysInYear) break
        remainingDays -= daysInYear
        year++
    }

    val monthDays = intArrayOf(31, if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var month = 0
    while (month < 12 && remainingDays >= monthDays[month]) {
        remainingDays -= monthDays[month]
        month++
    }
    val day = remainingDays + 1
    month += 1

    return "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/$year " +
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}

