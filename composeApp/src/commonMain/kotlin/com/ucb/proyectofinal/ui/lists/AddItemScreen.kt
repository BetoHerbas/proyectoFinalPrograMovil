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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun AddItemScreen(
    onCancel: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Libro") }
    var selectedPriority by remember { mutableStateOf("Hi") }
    var selectedStatus by remember { mutableStateOf("Por empezar") }
    var note by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf("#Ficción")) }

    val types = listOf("📚 Libro", "🎬 Película", "🎙️ Podcast")
    val priorities = listOf("Hi", "Md", "Lo")

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
            Text(
                text = "Cancelar",
                fontSize = 15.sp,
                color = AppColors.TextSecondary,
                modifier = Modifier.clickable { onCancel() }
            )
            Text("Añadir Nuevo Elemento", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Text(
                text = "Guardar",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary,
                modifier = Modifier.clickable { onSave() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // ── Selector de tipo ─────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                types.forEach { type ->
                    val isSelected = type.contains(selectedType)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AppColors.PrimaryContainer else AppColors.Surface)
                            .border(1.dp, if (isSelected) AppColors.Primary else AppColors.SurfaceHighlight, RoundedCornerShape(12.dp))
                            .clickable { selectedType = if ("Libro" in type) "Libro" else if ("Película" in type) "Película" else "Podcast" }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = type,
                            fontSize = 13.sp,
                            color = if (isSelected) AppColors.Primary else AppColors.TextSecondary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Título ───────────────────────────────────────────────────────
            SectionLabel("TÍTULO")
            Spacer(modifier = Modifier.height(8.dp))
            AddItemTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Ej. El Gran Gatsby"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Autor / Director ─────────────────────────────────────────────
            SectionLabel("AUTOR / DIRECTOR")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nombre del creador", color = AppColors.TextDisabled, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Person, null, tint = AppColors.TextSecondary, modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = addItemFieldColors(),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = AppColors.TextPrimary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Prioridad y Estado ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("PRIORIDAD")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppColors.Surface)
                            .border(1.dp, AppColors.SurfaceHighlight, RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        priorities.forEach { p ->
                            val sel = p == selectedPriority
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (sel) AppColors.Primary else Color.Transparent)
                                    .clickable { selectedPriority = p }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p, fontSize = 13.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, color = if (sel) AppColors.Background else AppColors.TextSecondary)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("ESTADO")
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppColors.Surface)
                            .border(1.dp, AppColors.SurfaceHighlight, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedStatus, fontSize = 13.sp, color = AppColors.TextPrimary)
                            Text("▾", fontSize = 14.sp, color = AppColors.TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Nota personal ────────────────────────────────────────────────
            SectionLabel("NOTA PERSONAL")
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.SurfaceHighlight, RoundedCornerShape(12.dp))
            ) {
                Column {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { if (it.length <= 500) note = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = {
                            Text(
                                "¿Por qué quieres ver/leer esto? Añade tus pensamientos...",
                                color = AppColors.TextDisabled, fontSize = 13.sp, lineHeight = 18.sp
                            )
                        },
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = addItemFieldColors(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = AppColors.TextPrimary)
                    )
                    // Toolbar mini
                    Row(
                        modifier = Modifier.fillMaxWidth().background(AppColors.SurfaceVariant).padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("B", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)
                            Text("I", fontSize = 14.sp, color = AppColors.TextSecondary)
                            Text("🔗", fontSize = 14.sp, color = AppColors.TextSecondary)
                        }
                        Text("${note.length}/500", fontSize = 11.sp, color = AppColors.TextDisabled)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Etiquetas ────────────────────────────────────────────────────
            SectionLabel("ETIQUETAS")
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppColors.PrimaryContainer)
                            .border(1.dp, AppColors.Primary, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tag, fontSize = 12.sp, color = AppColors.Primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("×", fontSize = 14.sp, color = AppColors.Primary)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, AppColors.SurfaceHighlight, RoundedCornerShape(20.dp))
                        .clickable { }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("+ Añadir etiqueta", fontSize = 12.sp, color = AppColors.TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botón principal ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Primary)
                    .clickable { onSave() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("⊕  Añadir a la lista", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.Background)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AddItemTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = AppColors.TextDisabled, fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = addItemFieldColors(),
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = AppColors.TextPrimary)
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary, letterSpacing = 1.sp)
}

@Composable
private fun addItemFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AppColors.Primary,
    unfocusedBorderColor = AppColors.SurfaceHighlight,
    focusedContainerColor = AppColors.Surface,
    unfocusedContainerColor = AppColors.Surface,
    cursorColor = AppColors.Primary
)
