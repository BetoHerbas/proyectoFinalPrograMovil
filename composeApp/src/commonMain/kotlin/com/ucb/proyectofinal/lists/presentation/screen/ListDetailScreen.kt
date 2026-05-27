package com.ucb.proyectofinal.lists.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

// ─── Estilos Locales Consistentes ───
private val BgDark = Color(0xFF0B1D29)
private val BgTeal = Color(0xFF0A3736)
private val BgDeep = Color(0xFF0D1B2D)
private val Accent = Color(0xFF00E5B6)
private val AccentBright = Color(0xFF22F2D4)
private val CardBg = Color(0xFF1A2421)
private val TextPrimary = Color(0xFFE8FAFF)
private val TextSecondary = Color(0xFF8FB3BC)
private val TextMuted = Color(0xFF6F94A2)

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
    onNavigateToItemDetail: (String) -> Unit,
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
            ) { Icon(Icons.Default.Add, "Add") }
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors = listOf(BgDark, BgTeal, BgDeep)))
                .padding(padding),
            contentPadding = PaddingValues(bottom = 92.dp)
        ) {
            item {
                Spacer(modifier = Modifier.statusBarsPadding())
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary)
                    }
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, null, tint = AccentBright)
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = state.listName.ifEmpty { listName },
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(filteredItems, key = { it.id.value }) { item ->
                ContentItemCard(
                    item = item,
                    onToggleSeen = { viewModel.onIntent(ListDetailIntent.ToggleSeen(item)) },
                    onDelete = { viewModel.onIntent(ListDetailIntent.DeleteItem(item)) },
                    onClick = { onNavigateToItemDetail(item.title.value) } // PASAMOS EL TÍTULO AQUÍ
                )
            }
        }
    }
}

@Composable
private fun ContentItemCard(item: ContentItem, onToggleSeen: () -> Unit, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggleSeen, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (item.seen) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (item.seen) Accent else TextMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title.value, color = TextPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                Text("Ver detalles", color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = TextMuted.copy(0.4f), modifier = Modifier.size(18.dp))
            }
        }
    }
}
