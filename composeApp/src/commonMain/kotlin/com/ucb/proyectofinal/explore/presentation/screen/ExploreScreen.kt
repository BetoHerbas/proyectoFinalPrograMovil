package com.ucb.proyectofinal.explore.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.proyectofinal.explore.presentation.viewmodel.ExploreViewModel
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.navigation.AppBottomBar
import com.ucb.proyectofinal.navigation.BottomTab
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExploreScreen(
    onNavigateToDetail: (listId: String, listName: String, listType: ContentType, description: String, coverImageUrl: String?, isPublic: Boolean) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ExploreViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<ContentType?>(null) }

    val filteredLists = remember(state.lists, searchQuery, selectedFilter) {
        state.lists.filter { list ->
            val matchesQuery = list.name.value.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedFilter == null || list.type == selectedFilter
            matchesQuery && matchesType
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                currentTab = BottomTab.EXPLORE,
                onNavigateToHome = onNavigateToHome,
                onNavigateToExplore = {},
                onNavigateToCreate = onNavigateToCreate,
                onNavigateToFavorites = onNavigateToFavorites,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { padding ->
        val bg = MaterialTheme.colorScheme.background
        val onBg = MaterialTheme.colorScheme.onBackground
        val onBgSub = MaterialTheme.colorScheme.onSurfaceVariant
        val cardBg = MaterialTheme.colorScheme.surfaceVariant
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(bg, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), bg)
                    )
                )
                .padding(padding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Explorar",
                style = MaterialTheme.typography.headlineLarge,
                color = onBg,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Descubre listas públicas",
                style = MaterialTheme.typography.bodyMedium,
                color = onBgSub
            )

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF8AAAB5))
                },
                placeholder = { Text("Buscar listas públicas...", color = Color(0xFF6F94A2)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = Color(0xFF2AE2D0),
                    unfocusedBorderColor = Color(0x223AD8C7),
                    focusedTextColor = onBg,
                    unfocusedTextColor = onBg,
                    cursorColor = Color(0xFF2AE2D0)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Todo") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF22F2D4),
                        selectedLabelColor = Color(0xFF043F40),
                        containerColor = cardBg,
                        labelColor = onBgSub
                    )
                )
                ContentType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = { selectedFilter = type },
                        label = { Text(exploreTypeLabel(type)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF22F2D4),
                            selectedLabelColor = Color(0xFF043F40),
                            containerColor = cardBg,
                            labelColor = onBgSub
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF22F2D4))
                    }
                }
                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.error!!, color = onBgSub)
                    }
                }
                filteredLists.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay listas públicas disponibles", color = onBgSub)
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 92.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredLists, key = { it.id.value }) { list ->
                            ExploreListCard(
                                list = list,
                                isFavorite = state.favoriteIds.contains(list.id.value),
                                onClick = {
                                    onNavigateToDetail(
                                        list.id.value,
                                        list.name.value,
                                        list.type,
                                        list.description,
                                        list.coverImageUrl,
                                        list.isPublic
                                    )
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(list) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreListCard(
    list: ContentList,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0x223EE5D0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .background(exploreTypeBrush(list.type), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = exploreTypeIcon(list.type),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = list.name.value,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = exploreSubtitleByType(list.type),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${list.itemCount} elementos",
                    color = Color(0xFF5E8A96),
                    style = MaterialTheme.typography.labelSmall
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (isFavorite) Color(0xFFFF4F6D) else Color(0xFF8AAAB5),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun exploreTypeLabel(type: ContentType) = when (type) {
    ContentType.MOVIE -> "Películas"
    ContentType.SERIES -> "Series"
    ContentType.BOOK -> "Libros"
    ContentType.VIDEOGAME -> "Videojuegos"
}

private fun exploreTypeIcon(type: ContentType) = when (type) {
    ContentType.MOVIE -> "🎬"
    ContentType.SERIES -> "📺"
    ContentType.BOOK -> "📚"
    ContentType.VIDEOGAME -> "🎮"
}

private fun exploreSubtitleByType(type: ContentType) = when (type) {
    ContentType.MOVIE -> "Lista de películas"
    ContentType.SERIES -> "Lista de series"
    ContentType.BOOK -> "Lista de libros"
    ContentType.VIDEOGAME -> "Lista de videojuegos"
}

private fun exploreTypeBrush(type: ContentType) = when (type) {
    ContentType.MOVIE -> Brush.linearGradient(listOf(Color(0xFF1A3A5C), Color(0xFF0D2236)))
    ContentType.SERIES -> Brush.linearGradient(listOf(Color(0xFF1A4A3A), Color(0xFF0D2E22)))
    ContentType.BOOK -> Brush.linearGradient(listOf(Color(0xFF3A2A1A), Color(0xFF221A0D)))
    ContentType.VIDEOGAME -> Brush.linearGradient(listOf(Color(0xFF2A1A3A), Color(0xFF1A0D22)))
}
