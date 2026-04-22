package com.ucb.proyectofinal.ui.sync

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.proyectofinal.core.data.db.TodoEntity
import org.koin.compose.viewmodel.koinViewModel

// Color palette
private val BgDark       = Color(0xFF0D1117)
private val Surface1     = Color(0xFF161B22)
private val Surface2     = Color(0xFF21262D)
private val AccentGreen  = Color(0xFF3FB950)
private val AccentOrange = Color(0xFFF0883E)
private val AccentBlue   = Color(0xFF58A6FF)
private val AccentPurple = Color(0xFF8B5CF6)
private val TextPrimary  = Color(0xFFE6EDF3)
private val TextSecondary= Color(0xFF8B949E)

/**
 * Pantalla de demo de la Cola de Sincronización Offline.
 *
 * Demuestra:
 *  ① Room  → ítem guardado localmente con isPending=true
 *  ② WorkManager → servicio en 2do plano, se dispara con red disponible
 *  ③ Firebase RTDB → destino de los datos sincronizados
 *  ④ Notificación local → resumen al terminar la sincronización
 */
@Composable
fun OfflineSyncDemoScreen(
    viewModel: OfflineSyncViewModel = koinViewModel()
) {
    val todos by viewModel.todos.collectAsState()

    var titleInput by remember { mutableStateOf("") }
    var descInput  by remember { mutableStateOf("") }

    val pendingCount   = todos.count { it.isPending }
    val syncedCount    = todos.count { !it.isPending }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Header ──────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cola Offline",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Sincronización con Firebase RTDB",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                // Badge de pendientes
                if (pendingCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AccentOrange)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pendingCount pendiente${if (pendingCount > 1) "s" else ""}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Stats chips ─────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip(label = "Total",      value = "${todos.size}",   color = AccentBlue)
                StatChip(label = "Pendientes", value = "$pendingCount",   color = AccentOrange)
                StatChip(label = "Subidos",    value = "$syncedCount",    color = AccentGreen)
            }

            Spacer(Modifier.height(24.dp))

            // ── Card "Agregar nota offline" ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface1),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "① Guardar en Room → sync automático al conectar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentBlue,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Título", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = Surface2,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Descripción", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = Surface2,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.addTodo(titleInput, descInput)
                            titleInput = ""
                            descInput  = ""
                        },
                        enabled = titleInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            disabledContainerColor = Surface2
                        )
                    ) {
                        Text(
                            text = "Agregar nota",
                            fontWeight = FontWeight.SemiBold,
                            color = if (titleInput.isNotBlank()) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Leyenda ──────────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LegendItem(color = AccentOrange, label = "⏳ Pendiente (en Room)")
                LegendItem(color = AccentGreen,  label = "✅ Subido a RTDB")
            }

            Spacer(Modifier.height(12.dp))

            // ── Lista de ítems ───────────────────────────────────────────────
            if (todos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Sin notas todavía",
                            color = TextSecondary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Agrega una nota para probar la cola offline",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(todos, key = { it.id }) { item ->
                        TodoItemCard(item)
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Surface1),
        modifier = Modifier.wrapContentWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun TodoItemCard(item: TodoEntity) {
    val borderColor by animateColorAsState(
        targetValue = if (item.isPending) AccentOrange else AccentGreen,
        animationSpec = tween(durationMillis = 600),
        label = "borderColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface1),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Indicador de estado
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(borderColor)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!item.isPending && item.syncedAt > 0) {
                    Text(
                        text = "Subido a Firebase RTDB",
                        color = AccentGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Chip de estado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(borderColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (item.isPending) "⏳" else "✅",
                    fontSize = 14.sp
                )
            }
        }
    }
}
