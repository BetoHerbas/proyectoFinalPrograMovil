package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.LocalMovies
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.presentation.effect.ContentListsEffect
import com.ucb.proyectofinal.lists.presentation.intent.ContentListsIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.ContentListsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateListScreen(
    onNavigateBack: () -> Unit,
    viewModel: ContentListsViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(ContentType.MOVIE) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            if (effect is ContentListsEffect.ShowSuccess) onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0A1D26), Color(0xFF072F2C), Color(0xFF061A25))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("Cancelar", color = Color(0xFFBCD1D8), style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    "Nueva Lista",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        viewModel.onIntent(
                            ContentListsIntent.CreateList(
                                name = name,
                                type = selectedType,
                                description = description,
                                coverImageUrl = null,
                                isPublic = !isPrivate
                            )
                        )
                    },
                    shape = RoundedCornerShape(999.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1CF1D2),
                        contentColor = Color(0xFF043636)
                    )
                ) {
                    Text("Guardar", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .border(
                        border = BorderStroke(1.dp, Color(0x3346D4C3)),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(Color(0x2223404C), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(Color(0x22394956), RoundedCornerShape(999.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (selectedType) {
                                ContentType.BOOK -> Icons.Outlined.MenuBook
                                ContentType.MOVIE -> Icons.Outlined.LocalMovies
                                ContentType.SERIES -> Icons.Outlined.LiveTv
                            },
                            contentDescription = null,
                            tint = Color(0xFF88A9B1)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Portada por defecto: ${defaultCoverLabel(selectedType)}",
                        color = Color(0xFF8BAAB2),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text("NOMBRE DE LA LISTA", color = Color(0xFF23E9D0), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Ej. Mis libros favoritos 2024", color = Color(0xFF72919A)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedBorderColor = Color(0x5538DBC9),
                    unfocusedBorderColor = Color(0x3335AFA3),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF1CE8C1)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text("DESCRIPCIÓN", color = Color(0xFF23E9D0), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 150) description = it
                },
                placeholder = { Text("¿De qué trata esta lista? Añade algunos detalles...", color = Color(0xFF72919A)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x44233542),
                    unfocusedContainerColor = Color(0x44233542),
                    focusedBorderColor = Color(0x5538DBC9),
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF1CE8C1)
                )
            )
            Text(
                text = "${description.length}/150",
                color = Color(0xFF6E8A93),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Categoría", color = Color.White, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(10.dp))

            val categoryCards = listOf(
                Triple(ContentType.BOOK, "Libros", Icons.Outlined.MenuBook),
                Triple(ContentType.MOVIE, "Películas", Icons.Outlined.LocalMovies),
                Triple(ContentType.SERIES, "Series", Icons.Outlined.LiveTv)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                categoryCards.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { (type, label, icon) ->
                            val selected = selectedType == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                onClick = { selectedType = type },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Color(0x3315D9C1) else Color(0x33223743),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selected) Color(0xFF1FE8C8) else Color(0x2238C7B6)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(Color(0x33425564), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = Color(0xFFD7EAF0), modifier = Modifier.size(17.dp))
                                    }
                                    Text(
                                        text = label,
                                        color = Color(0xFFE2F1F5),
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = Color(0x2236B4AA))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Lista Privada", color = Color.White, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Solo tú podrás ver esta lista",
                        color = Color(0xFF8AA7AF),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = isPrivate,
                    onCheckedChange = { isPrivate = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF1CE8C1),
                        uncheckedThumbColor = Color(0xFFE1ECEF),
                        uncheckedTrackColor = Color(0xFF536D75)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun defaultCoverLabel(type: ContentType): String = when (type) {
    ContentType.BOOK -> "Libros"
    ContentType.MOVIE -> "Películas"
    ContentType.SERIES -> "Series"
}
