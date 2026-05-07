package com.ucb.proyectofinal.lists.presentation.screen

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.presentation.effect.ContentListsEffect
import com.ucb.proyectofinal.lists.presentation.intent.ContentListsIntent
import com.ucb.proyectofinal.lists.presentation.viewmodel.ContentListsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContentListsScreen(
    onNavigateToDetail: (listId: String, listName: String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ContentListsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<ContentType?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ContentListsEffect.NavigateToDetail -> onNavigateToDetail(effect.listId, effect.listName)
                is ContentListsEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is ContentListsEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    val filteredLists = remember(state.lists, searchQuery, selectedFilter) {
        state.lists.filter { list ->
            val supportedType = list.type in supportedTypes
            val matchesQuery = list.name.value.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedFilter == null || list.type == selectedFilter
            supportedType && matchesQuery && matchesType
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(color = Color(0xFF0F2730)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomAction(
                        label = "Listas",
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.GridView, contentDescription = null) }
                    )

                    FloatingActionButton(
                        onClick = onNavigateToCreate,
                        containerColor = Color(0xFF22F2D4),
                        contentColor = Color(0xFF043F40),
                        modifier = Modifier.size(58.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar lista")
                    }

                    BottomAction(
                        label = "Favoritos",
                        selected = false,
                        onClick = onNavigateToSettings,
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0B1D29), Color(0xFF0A3736), Color(0xFF0D1B2D))
                    )
                )
                .padding(padding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Mis Listas",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(0xFFE8FAFF),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Organiza tu mundo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFA8C8D1)
                    )
                }

                IconButton(onClick = onNavigateToProfile) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFF2B4B56),
                        border = BorderStroke(1.dp, Color(0x5538E3D1))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF2D5565), Color(0xFF1C313A))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color(0xFFD9F6FF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF8AAAB5))
                },
                placeholder = { Text("Buscar en mis listas...", color = Color(0xFF6F94A2)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xAA102D3A),
                    unfocusedContainerColor = Color(0xAA102D3A),
                    focusedBorderColor = Color(0xFF2AE2D0),
                    unfocusedBorderColor = Color(0x223AD8C7),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
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
                        containerColor = Color(0x55304653),
                        labelColor = Color(0xFFD1E8EF)
                    )
                )
                supportedTypes.forEach { type ->
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = { selectedFilter = type },
                        label = { Text(typeChipLabel(type)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF22F2D4),
                            selectedLabelColor = Color(0xFF043F40),
                            containerColor = Color(0x55304653),
                            labelColor = Color(0xFFD1E8EF)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recientes",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFF0FBFF),
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToSettings) {
                    Text(
                        text = "VER TODO",
                        color = Color(0xFF23E8D3),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF22F2D4))
                    }
                }

                filteredLists.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron listas", color = Color(0xFF8AAAB5))
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
                            ContentListCard(
                                list = list,
                                onClick = {
                                    viewModel.onIntent(
                                        ContentListsIntent.NavigateToDetail(
                                            list.id.value,
                                            list.name.value
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private val supportedTypes = listOf(
    ContentType.MOVIE,
    ContentType.SERIES,
    ContentType.BOOK
)

@Composable
private fun BottomAction(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val contentColor = if (selected) Color(0xFF22F2D4) else Color(0xFFA2BBC5)
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                icon()
            }
            Text(text = label, color = contentColor, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun ContentListCard(
    list: ContentList,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0x223EE5D0)),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC163545))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .background(typeBrush(list.type), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = typeIcon(list.type),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = list.name.value,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitleByType(list.type),
                color = Color(0xFF8DB2BD),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${list.itemCount} ítems",
                color = Color(0xFF2BE2CF),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun typeBrush(type: ContentType): Brush = when (type) {
    ContentType.MOVIE -> Brush.linearGradient(listOf(Color(0xFF071B26), Color(0xFF113D5E)))
    ContentType.SERIES -> Brush.linearGradient(listOf(Color(0xFF26273A), Color(0xFF4B5C90)))
    ContentType.BOOK -> Brush.linearGradient(listOf(Color(0xFF1F2D34), Color(0xFF496678)))
}

private fun typeIcon(type: ContentType): String = when (type) {
    ContentType.MOVIE -> "🎬"
    ContentType.SERIES -> "📺"
    ContentType.BOOK -> "📚"
}

private fun typeChipLabel(type: ContentType): String = when (type) {
    ContentType.MOVIE -> "Películas"
    ContentType.SERIES -> "Series"
    ContentType.BOOK -> "Libros"
}

private fun subtitleByType(type: ContentType): String = when (type) {
    ContentType.MOVIE -> "Sesión de cine"
    ContentType.SERIES -> "Para maratón"
    ContentType.BOOK -> "Próximas lecturas"
}
