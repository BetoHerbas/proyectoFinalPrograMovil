package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.presentation.effect.ListDetailEffect
import com.ucb.proyectofinal.lists.presentation.intent.ListDetailIntent
import com.ucb.proyectofinal.lists.presentation.state.ItemFilter
import com.ucb.proyectofinal.lists.presentation.viewmodel.ListDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

// ─── Color palette (consistent with the rest of the app) ───
private val BgDark = Color(0xFF0B1D29)
private val BgTeal = Color(0xFF0A3736)
private val BgDeep = Color(0xFF0D1B2D)
private val Accent = Color(0xFF00E5B6)
private val AccentBright = Color(0xFF22F2D4)
private val CardBg = Color(0xFF1A2421)
private val TextPrimary = Color(0xFFE8FAFF)
private val TextSecondary = Color(0xFF8FB3BC)
private val TextMuted = Color(0xFF6F94A2)
private val ChipBg = Color(0xFF1C2E38)
private val ChipSelectedBg = Color(0xFF22F2D4)
private val ChipSelectedText = Color(0xFF043F40)

@Composable
fun ListDetailScreen(
    listId: String,
    listName: String,
    listType: String,
    description: String = "",
    coverImageUrl: String? = null,
    isPublic: Boolean = true,
    onNavigateBack: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: ListDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(listId) {
        viewModel.onIntent(
            ListDetailIntent.LoadDetail(
                listId = listId,
                listName = listName,
                description = description,
                coverImageUrl = coverImageUrl,
                isPublic = isPublic,
                listType = listType
            )
        )
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

    // Derived values
    val displayName = state.listName.ifEmpty { listName }
    val displayDescription = state.description.ifEmpty { description }
    val contentType = runCatching { ContentType.valueOf(state.listType.ifEmpty { listType }) }
        .getOrDefault(ContentType.MOVIE)
    val totalItems = state.items.size
    val completedItems = state.items.count { it.seen }
    val progress = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f
    val progressPercent = (progress * 100).toInt()

    val filteredItems = remember(state.items, state.selectedFilter) {
        when (state.selectedFilter) {
            ItemFilter.ALL -> state.items
            ItemFilter.COMPLETED -> state.items.filter { it.seen }
            ItemFilter.PENDING -> state.items.filter { !it.seen }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddItem,
                containerColor = AccentBright,
                contentColor = Color(0xFF043F40),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ítem")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(BgDark, BgTeal, BgDeep)
                    )
                )
                .padding(padding),
            contentPadding = PaddingValues(bottom = 92.dp)
        ) {
            // ─── Top bar ───
            item {
                Spacer(modifier = Modifier.statusBarsPadding())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                    Row {
                        IconButton(onClick = { /* Placeholder: Compartir */ }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Compartir",
                                tint = TextPrimary
                            )
                        }
                        IconButton(onClick = onNavigateToEdit) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = AccentBright
                            )
                        }
                    }
                }
            }

            // ─── Cover image area with title overlay ───
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(coverGradient(contentType))
                ) {
                    // Category emoji large in center
                    Text(
                        text = typeEmoji(contentType),
                        fontSize = 56.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Dark overlay at the bottom for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(120.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC0B1D29)
                                    )
                                )
                            )
                    )

                    // Badges + title overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        // Badges row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Public/Private badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Accent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (state.isPublic) "LISTA PÚBLICA" else "LISTA PRIVADA",
                                    color = Accent,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            // Category badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "•${categoryLabel(contentType)}",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title
                        Text(
                            text = displayName,
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // ─── Description ───
            if (displayDescription.isNotBlank()) {
                item {
                    Text(
                        text = displayDescription,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        lineHeight = 22.sp
                    )
                }
            }

            // ─── Progress bar ───
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = if (displayDescription.isBlank()) 12.dp else 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$completedItems de $totalItems Completados",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$progressPercent%",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Accent,
                        trackColor = Color(0xFF1C2E38)
                    )
                }
            }

            // ─── Filter chips ───
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        label = "Todos",
                        selected = state.selectedFilter == ItemFilter.ALL,
                        onClick = { viewModel.onIntent(ListDetailIntent.ChangeFilter(ItemFilter.ALL)) }
                    )
                    FilterChipItem(
                        label = "Pendientes",
                        selected = state.selectedFilter == ItemFilter.PENDING,
                        onClick = { viewModel.onIntent(ListDetailIntent.ChangeFilter(ItemFilter.PENDING)) }
                    )
                    FilterChipItem(
                        label = "Completados",
                        selected = state.selectedFilter == ItemFilter.COMPLETED,
                        onClick = { viewModel.onIntent(ListDetailIntent.ChangeFilter(ItemFilter.COMPLETED)) }
                    )
                }
            }

            // ─── Content ───
            when {
                state.isLoading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Accent)
                    }
                }
                filteredItems.isEmpty() -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (state.selectedFilter) {
                                ItemFilter.ALL -> "No hay ítems. ¡Agrega uno!"
                                ItemFilter.COMPLETED -> "No hay ítems completados"
                                ItemFilter.PENDING -> "¡Todos los ítems completados! 🎉"
                            },
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> items(filteredItems, key = { it.id.value }) { item ->
                    ContentItemCard(
                        item = item,
                        onToggleSeen = { viewModel.onIntent(ListDetailIntent.ToggleSeen(item)) },
                        onRate = { rating -> viewModel.onIntent(ListDetailIntent.RateItem(item, rating)) },
                        onDelete = { viewModel.onIntent(ListDetailIntent.DeleteItem(item)) }
                    )
                }
            }
        }
    }
}

// ─── Filter chip component ───
@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) ChipSelectedBg else ChipBg,
        animationSpec = tween(250)
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) ChipSelectedText else TextSecondary,
        animationSpec = tween(250)
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// ─── Item card component ───
@Composable
private fun ContentItemCard(
    item: ContentItem,
    onToggleSeen: () -> Unit,
    onRate: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular checkbox
            IconButton(
                onClick = onToggleSeen,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (item.seen) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (item.seen) "Marcar como pendiente" else "Marcar como visto",
                    tint = if (item.seen) Accent else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Title + type
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title.value,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = typeSublabel(item.type),
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Category tag chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = categoryChipColor(item.type).copy(alpha = 0.15f)
            ) {
                Text(
                    text = categoryLabel(item.type),
                    color = categoryChipColor(item.type),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Rating display (if rated)
            if (item.rating != null && item.rating.value > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "★${item.rating.value}.0",
                    color = Accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Star rating row
        Row(
            modifier = Modifier
                .padding(start = 56.dp, end = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            (1..5).forEach { star ->
                TextButton(
                    onClick = { onRate(star) },
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier.size(28.dp)
                ) {
                    Text(
                        if ((item.rating?.value ?: 0) >= star) "★" else "☆",
                        color = if ((item.rating?.value ?: 0) >= star) Accent else TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// ─── Helper functions ───

private fun coverGradient(type: ContentType): Brush = when (type) {
    ContentType.MOVIE -> Brush.linearGradient(
        listOf(Color(0xFF071B26), Color(0xFF1A3D5E), Color(0xFF0D2840))
    )
    ContentType.SERIES -> Brush.linearGradient(
        listOf(Color(0xFF1A1B2E), Color(0xFF3B4C7A), Color(0xFF26273A))
    )
    ContentType.BOOK -> Brush.linearGradient(
        listOf(Color(0xFF14232B), Color(0xFF2D5468), Color(0xFF1F3844))
    )
    ContentType.VIDEOGAME -> Brush.linearGradient(
        listOf(Color(0xFF142214), Color(0xFF2E5C35), Color(0xFF1C2B1C))
    )
}

private fun typeEmoji(type: ContentType): String = when (type) {
    ContentType.MOVIE -> "🎬"
    ContentType.SERIES -> "📺"
    ContentType.BOOK -> "📚"
    ContentType.VIDEOGAME -> "🎮"
}

private fun categoryLabel(type: ContentType): String = when (type) {
    ContentType.MOVIE -> "Película"
    ContentType.SERIES -> "Serie"
    ContentType.BOOK -> "Libro"
    ContentType.VIDEOGAME -> "Videojuego"
}

private fun typeSublabel(type: ContentType): String = when (type) {
    ContentType.MOVIE -> "Película"
    ContentType.SERIES -> "Serie de TV"
    ContentType.BOOK -> "Libro"
    ContentType.VIDEOGAME -> "Videojuego"
}

private fun categoryChipColor(type: ContentType): Color = when (type) {
    ContentType.MOVIE -> Color(0xFF64B5F6)
    ContentType.SERIES -> Color(0xFFBA68C8)
    ContentType.BOOK -> Color(0xFF81C784)
    ContentType.VIDEOGAME -> Color(0xFFFFB74D)
}
