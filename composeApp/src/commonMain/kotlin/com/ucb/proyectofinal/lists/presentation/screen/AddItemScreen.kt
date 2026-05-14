package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.presentation.effect.AddItemEffect
import com.ucb.proyectofinal.lists.presentation.intent.AddItemIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.AddItemViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddItemScreen(
    listId: String,
    listName: String,
    listType: ContentType,
    onNavigateBack: () -> Unit,
    viewModel: AddItemViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(listId) {
        viewModel.onIntent(AddItemIntent.Init(listId, listName, listType))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AddItemEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is AddItemEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFF030C12)
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF050E15), Color(0xFF04171F), Color(0xFF030A10))
                    )
                )
                .padding(padding)
                .padding(horizontal = 16.dp)
    ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
                Text(
                    text = "AGREGAR A ${state.listName.ifBlank { listName }.uppercase()}",
                    color = Color(0xFF12F1D8),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onIntent(AddItemIntent.QueryChanged(it)) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF8BB7C5))
                },
                trailingIcon = {
                    TextButton(onClick = { viewModel.onIntent(AddItemIntent.Search) }) {
                        Text("Buscar", color = Color(0xFF12F1D8))
                    }
                },
                placeholder = { Text("Buscar en el universo...", color = Color(0xFF5E808E)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0A121A),
                    unfocusedContainerColor = Color(0xFF0A121A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF12F1D8),
                    unfocusedBorderColor = Color(0xFF183341),
                    cursorColor = Color(0xFF12F1D8)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (state.query.isBlank()) {
                    "Top 10 ${labelForType(state.listType)}"
                } else {
                    "Resultados ${labelForType(state.listType)}"
                },
                color = Color(0xFFA8CDD8),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF12F1D8))
                }
            } else if (state.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No se encontraron resultados para ${labelForType(state.listType).lowercase()}",
                        color = Color(0xFF7398A5)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.items, key = { it.sourceId }) { item ->
                        SearchItemCard(
                            item = item,
                            isAdding = state.addingItemId == item.sourceId,
                            onAdd = { viewModel.onIntent(AddItemIntent.AddToList(item)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchItemCard(
    item: CatalogSearchItem,
    isAdding: Boolean,
    onAdd: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xAA0A1821)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0A131C))
                    .border(1.dp, Color(0x2235E8D2), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!item.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = "Portada de ${item.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = item.title.take(1).ifBlank { "?" },
                        color = Color(0xFF12F1D8),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.subtitle,
                color = Color(0xFF90AFBA),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                FilledIconButton(
                    onClick = onAdd,
                    enabled = !isAdding,
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFF0D2630),
                        contentColor = Color(0xFF12F1D8)
                    )
                ) {
                    if (isAdding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF12F1D8),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            }
        }
    }
}

private fun labelForType(type: ContentType): String = when (type) {
    ContentType.MOVIE -> "Películas"
    ContentType.SERIES -> "Series"
    ContentType.BOOK -> "Libros"
    ContentType.VIDEOGAME -> "Videojuegos"
}
