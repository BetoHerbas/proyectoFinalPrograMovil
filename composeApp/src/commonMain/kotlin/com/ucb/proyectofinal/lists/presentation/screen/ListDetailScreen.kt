package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.presentation.effect.ListDetailEffect
import com.ucb.proyectofinal.lists.presentation.intent.ListDetailIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.ListDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ListDetailScreen(
    listId: String,
    listName: String,
    listType: String,
    onNavigateBack: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    viewModel: ListDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(listId) {
        viewModel.onIntent(ListDetailIntent.LoadDetail(listId, listName))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ListDetailEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is ListDetailEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                is ListDetailEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddItem,
                containerColor = Color(0xFF00E5B6),
                contentColor = Color(0xFF101715)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ítem")
            }
        },
        containerColor = Color(0xFF101715)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text(
                    text = state.listName.ifEmpty { listName },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(
                text = "Tipo: ${runCatching { ContentType.valueOf(listType) }.getOrDefault(ContentType.MOVIE).name}",
                color = Color(0xFF8FB3BC),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00E5B6))
                }
                state.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ítems. ¡Agrega uno!", color = Color.Gray)
                }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.items) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2421))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.seen,
                                    onCheckedChange = { viewModel.onIntent(ListDetailIntent.ToggleSeen(item)) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF00E5B6),
                                        uncheckedColor = Color.Gray
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title.value, color = Color.White, fontWeight = FontWeight.SemiBold)
                                    Text(item.type.name.lowercase(), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                }
                                // Star rating
                                Row {
                                    (1..5).forEach { star ->
                                        TextButton(
                                            onClick = { viewModel.onIntent(ListDetailIntent.RateItem(item, star)) },
                                            contentPadding = PaddingValues(2.dp),
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Text(
                                                if ((item.rating?.value ?: 0) >= star) "★" else "☆",
                                                color = if ((item.rating?.value ?: 0) >= star) Color(0xFF00E5B6) else Color.Gray,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = { viewModel.onIntent(ListDetailIntent.DeleteItem(item)) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
