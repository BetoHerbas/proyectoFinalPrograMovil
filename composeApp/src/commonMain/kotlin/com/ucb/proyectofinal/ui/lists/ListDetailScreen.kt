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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.proyectofinal.ui.theme.AppColors

private data class ListItem(
    val title: String,
    val author: String,
    val tag: String,
    val tagColor: Color,
    val isCompleted: Boolean,
    val rating: Float = 0f,
    val extra: String = ""
)

private val mockItems = listOf(
    ListItem("Dune", "Frank Herbert", "Classic", AppColors.TagPurple, true, 5.0f),
    ListItem("Project Hail Mary", "Andy Weir", "Space Opera", AppColors.TagBlue, false),
    ListItem("Neuromancer", "William Gibson", "Cyberpunk", AppColors.TagGreen, false, extra = "320pg"),
    ListItem("The Three-Body Prob...", "Cixin Liu", "Hard Sci-Fi", AppColors.TagRed, false),
    ListItem("Foundation", "Isaac Asimov", "Classic", AppColors.TagPurple, true, 4.5f),
    ListItem("Hyperion", "Dan Simmons", "Space Opera", AppColors.TagBlue, true),
)

private val tabs = listOf("All Items", "Pending", "Completed")

@Composable
fun ListDetailScreen(
    onBack: () -> Unit = {},
    onAddItem: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    val filteredItems = when (selectedTab) {
        1 -> mockItems.filter { !it.isCompleted }
        2 -> mockItems.filter { it.isCompleted }
        else -> mockItems
    }

    val completedCount = mockItems.count { it.isCompleted }
    val total = mockItems.size
    val progress = completedCount.toFloat() / total

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(AppColors.TagBlue.copy(alpha = 0.3f), AppColors.Background)
                            )
                        )
                ) {
                    // Contenido de fondo con emoji
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("🚀", fontSize = 72.sp)
                    }
                    // Botón atrás
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .clickable { onBack() }
                            .align(Alignment.TopStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    // Botones Editar / Compartir
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionChip("Editar")
                        ActionChip("Compartir")
                    }
                }
            }

            // ── Título y descripción ──────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text("Sci-Fi Books 2026", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A curated selection of upcoming space operas, cyberpunk tales, and thought-provoking hard sci-fi.",
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de progreso
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = AppColors.Primary,
                        trackColor = AppColors.SurfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$completedCount of $total Completed", fontSize = 12.sp, color = AppColors.TextSecondary)
                        Text("${(progress * 100).toInt()}%", fontSize = 12.sp, color = AppColors.Primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── Tabs ─────────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.Surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = index == selectedTab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AppColors.Primary else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) AppColors.Background else AppColors.TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Lista de ítems ───────────────────────────────────────────────
            items(filteredItems) { item ->
                ListItemRow(item = item)
            }
        }

        // ── FAB ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(AppColors.Primary)
                .clickable { onAddItem() },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 26.sp, fontWeight = FontWeight.Light, color = AppColors.Background)
        }
    }
}

@Composable
private fun ListItemRow(item: ListItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (item.isCompleted) AppColors.Primary else Color.Transparent)
                .border(2.dp, if (item.isCompleted) AppColors.Primary else AppColors.SurfaceHighlight, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (item.isCompleted) {
                Text("✓", fontSize = 14.sp, color = AppColors.Background, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.author, fontSize = 12.sp, color = AppColors.TextSecondary)
                if (item.extra.isNotEmpty()) {
                    Text("  ·  ${item.extra}", fontSize = 12.sp, color = AppColors.TextDisabled)
                }
                if (item.rating > 0) {
                    Text("  ★ ${item.rating}", fontSize = 12.sp, color = AppColors.TagOrange)
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(item.tagColor.copy(alpha = 0.18f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(item.tag, fontSize = 10.sp, color = item.tagColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ActionChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 12.sp, color = Color.White)
    }
}
