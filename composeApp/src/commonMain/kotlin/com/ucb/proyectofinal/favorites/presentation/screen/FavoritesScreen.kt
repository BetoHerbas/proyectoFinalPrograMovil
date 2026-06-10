package com.ucb.proyectofinal.favorites.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.proyectofinal.favorites.presentation.viewmodel.FavoritesViewModel
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.navigation.AppBottomBar
import com.ucb.proyectofinal.navigation.BottomTab
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetail: (listId: String, listName: String, listType: ContentType, description: String, coverImageUrl: String?, isPublic: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                currentTab = BottomTab.FAVORITES,
                onNavigateToHome = onNavigateToHome,
                onNavigateToExplore = onNavigateToExplore,
                onNavigateToCreate = onNavigateToCreate,
                onNavigateToFavorites = {},
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { padding ->
        val bg = MaterialTheme.colorScheme.background
        val onBg = MaterialTheme.colorScheme.onBackground
        val onBgSub = MaterialTheme.colorScheme.onSurfaceVariant
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
                text = "Favoritos",
                style = MaterialTheme.typography.headlineLarge,
                color = onBg,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Tus listas guardadas",
                style = MaterialTheme.typography.bodyMedium,
                color = onBgSub
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF22F2D4))
                    }
                }
                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.error!!, color = Color(0xFF8AAAB5))
                    }
                }
                state.favorites.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = Color(0xFF2AE2D0),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aún no tienes favoritos",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Explora listas públicas y guárdalas\naquí para acceder fácilmente",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 92.dp)
                    ) {
                        items(state.favorites, key = { it.id.value }) { list ->
                            FavoriteListCard(
                                list = list,
                                onClick = {
                                    onNavigateToDetail(
                                        list.id.value, list.name.value, list.type,
                                        list.description, list.coverImageUrl, list.isPublic
                                    )
                                },
                                onRemove = { viewModel.removeFavorite(list.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteListCard(
    list: ContentList,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0x223EE5D0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = typeIcon(list.type),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = list.name.value,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${list.itemCount} elementos · ${typeLabel(list.type)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = Color(0xFFFF4F6D)
                )
            }
        }
    }
}

private fun typeIcon(type: ContentType) = when (type) {
    ContentType.MOVIE -> "🎬"
    ContentType.SERIES -> "📺"
    ContentType.BOOK -> "📚"
    ContentType.VIDEOGAME -> "🎮"
}

private fun typeLabel(type: ContentType) = when (type) {
    ContentType.MOVIE -> "Películas"
    ContentType.SERIES -> "Series"
    ContentType.BOOK -> "Libros"
    ContentType.VIDEOGAME -> "Videojuegos"
}
