package com.ucb.proyectofinal.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.proyectofinal.ui.components.BottomNavBar
import com.ucb.proyectofinal.ui.components.BottomNavItem
import com.ucb.proyectofinal.ui.theme.AppColors

private data class MockList(
    val title: String,
    val subtitle: String,
    val itemCount: Int,
    val emoji: String,
    val accentColor: Color
)

private val mockLists = listOf(
    MockList("Sci-Fi 2026", "Próximas lecturas", 12, "🚀", AppColors.TagBlue),
    MockList("Anime to Start", "Prioridad alta", 5, "⭐", AppColors.TagPurple),
    MockList("Roadtrip Spots", "Verano 2025", 8, "📍", AppColors.TagGreen),
    MockList("Tech Podcasts", "Daily Updates", 21, "🎙️", AppColors.TagOrange),
    MockList("Best Cafes", "Para trabajar", 15, "☕", AppColors.TagRed),
    MockList("UX Research", "Referencias", 42, "📚", AppColors.TagBlue),
)

private val filterChips = listOf("Todo", "Libros", "Películas", "Podcasts", "Música", "Lugares")

@Composable
fun ContentListsScreen(
    onListClick: () -> Unit = {},
    onCreateList: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Todo") }
    var selectedNav by remember { mutableStateOf(BottomNavItem.Lists) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis Listas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                // Avatar minimal
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = AppColors.Primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            // ── Search bar ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.SurfaceHighlight, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("🔍  Buscar listas...", color = AppColors.TextDisabled, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Filter chips ─────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterChips) { chip ->
                    val selected = chip == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) AppColors.Primary else AppColors.Surface)
                            .border(1.dp, if (selected) AppColors.Primary else AppColors.SurfaceHighlight, RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = chip }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = chip,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) AppColors.Background else AppColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sección recientes ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recientes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Text("Ver todo", fontSize = 13.sp, color = AppColors.Primary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Grid de listas ───────────────────────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockLists) { list ->
                    ListCard(list = list, onClick = onListClick)
                }
            }
        }

        // ── FAB ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(AppColors.Primary)
                .clickable { onCreateList() },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 26.sp, fontWeight = FontWeight.Light, color = AppColors.Background)
        }

        // ── Bottom Nav ───────────────────────────────────────────────────────
        BottomNavBar(
            selected = selectedNav,
            onSelect = { item ->
                selectedNav = item
                if (item == BottomNavItem.Profile) onNavigateToProfile()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ListCard(list: MockList, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .clickable { onClick() }
            .aspectRatio(0.85f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Imagen placeholder con gradiente y emoji
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                list.accentColor.copy(alpha = 0.25f),
                                AppColors.SurfaceVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(list.emoji, fontSize = 40.sp)
            }

            // Info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = list.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = list.subtitle,
                    fontSize = 11.sp,
                    color = AppColors.TextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Chip de conteo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(list.accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${list.itemCount} elementos",
                        fontSize = 10.sp,
                        color = list.accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
