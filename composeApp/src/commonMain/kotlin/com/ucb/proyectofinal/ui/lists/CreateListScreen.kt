package com.ucb.proyectofinal.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

private data class Category(val emoji: String, val name: String)
private val categories = listOf(
    Category("📚", "Libros"),
    Category("🎬", "Películas"),
    Category("🎙️", "Podcasts"),
    Category("🎵", "Música"),
    Category("📍", "Lugares"),
)

@Composable
fun CreateListScreen(
    onCancel: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var listName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Libros") }
    var isPrivate by remember { mutableStateOf(false) }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Cancelar", fontSize = 15.sp, color = AppColors.TextSecondary, modifier = Modifier.clickable { onCancel() })
            Text("Nueva Lista", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Text("Guardar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Primary, modifier = Modifier.clickable { onSave() })
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // ── Imagen de portada ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.SurfaceVariant)
                    .border(2.dp, AppColors.SurfaceHighlight, RoundedCornerShape(16.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Añadir Portada", fontSize = 14.sp, color = AppColors.TextSecondary)
                    Text("Toca para subir una imagen", fontSize = 12.sp, color = AppColors.TextDisabled)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Nombre de la lista ───────────────────────────────────────────
            CreateListLabel("NOMBRE DE LA LISTA")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = listName,
                onValueChange = { listName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej. Mis libros favoritos 2024", color = AppColors.TextDisabled, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = createListFieldColors(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = AppColors.TextPrimary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Descripción ──────────────────────────────────────────────────
            CreateListLabel("DESCRIPCIÓN")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 150) description = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("¿De qué trata esta lista? Añade algunos detalles...", color = AppColors.TextDisabled, fontSize = 13.sp, lineHeight = 18.sp) },
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = createListFieldColors(),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = AppColors.TextPrimary),
                suffix = {
                    Box(modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)) {
                        Text("${description.length}/150", fontSize = 11.sp, color = AppColors.TextDisabled)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Categoría ────────────────────────────────────────────────────
            CreateListLabel("CATEGORÍA")
            Spacer(modifier = Modifier.height(12.dp))

            // Grid 2 columnas + última centrada
            val chunked = categories.chunked(2)
            chunked.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { cat ->
                        val isSelected = cat.name == selectedCategory
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) AppColors.PrimaryContainer else AppColors.Surface)
                                .border(1.5.dp, if (isSelected) AppColors.Primary else AppColors.SurfaceHighlight, RoundedCornerShape(14.dp))
                                .clickable { selectedCategory = cat.name }
                                .padding(vertical = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(cat.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = cat.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.Primary else AppColors.TextSecondary
                                )
                            }
                        }
                    }
                    // Si la fila tiene un solo elemento, llenar el espacio
                    if (row.size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Toggle Lista Privada ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Lista Privada", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
                        Text("Solo tú podrás ver esta lista", fontSize = 12.sp, color = AppColors.TextSecondary)
                    }
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppColors.Background,
                            checkedTrackColor = AppColors.Primary,
                            uncheckedThumbColor = AppColors.TextSecondary,
                            uncheckedTrackColor = AppColors.SurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botón crear ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Primary)
                    .clickable { onSave() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Crear lista →", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.Background)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CreateListLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary, letterSpacing = 1.sp)
}

@Composable
private fun createListFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AppColors.Primary,
    unfocusedBorderColor = AppColors.SurfaceHighlight,
    focusedContainerColor = AppColors.Surface,
    unfocusedContainerColor = AppColors.Surface,
    cursorColor = AppColors.Primary
)
