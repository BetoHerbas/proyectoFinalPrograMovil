package com.ucb.proyectofinal.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

private data class RecentActivity(
    val emoji: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val accentColor: Color
)

private val recentActivities = listOf(
    RecentActivity("📖", "Terminaste Dune", "Una obra maestra de la ciencia ficción. Definitivamente valió la pena cada página.", "2h", AppColors.TagBlue),
    RecentActivity("🎬", "Calificaste Inception", "★★★★★", "3d", AppColors.TagPurple),
)

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToLists: () -> Unit = {}
) {
    var selectedNav by remember { mutableStateOf(BottomNavItem.Profile) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D2020), AppColors.Background)
                        )
                    )
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AppColors.PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Alex Rivera", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                    Text("@rivera_reads", fontSize = 13.sp, color = AppColors.TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones Editar perfil + Ajustes
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(AppColors.Primary)
                                .clickable { }
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text("Editar Perfil", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Background)
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AppColors.Surface)
                                .clickable { onNavigateToSettings() }
                                .padding(8.dp)
                        ) {
                            Text("⚙️", fontSize = 18.sp)
                        }
                    }
                }
            }

            // ── Estadísticas ─────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Estadísticas", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Text("Viendo todo", fontSize = 12.sp, color = AppColors.TextSecondary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stat card grande - Libros
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Surface)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("📚  LIBROS", fontSize = 11.sp, color = AppColors.TextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("50", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("leídos", fontSize = 14.sp, color = AppColors.TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                        }
                    }
                    // Donut chart placeholder
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AppColors.PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("78%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mini-stat cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🎬",
                    count = "120",
                    label = "Películas Vistas",
                    accentColor = AppColors.TagPurple
                )
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🎙️",
                    count = "15",
                    label = "Podcasts Finalizados",
                    accentColor = AppColors.TagOrange
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Actividad reciente ───────────────────────────────────────────
            Text(
                text = "Actividad Reciente",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            recentActivities.forEach { activity ->
                ActivityRow(activity = activity)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ── Bottom Nav ───────────────────────────────────────────────────────
        BottomNavBar(
            selected = selectedNav,
            onSelect = { item ->
                selectedNav = item
                if (item == BottomNavItem.Lists) onNavigateToLists()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun MiniStatCard(
    modifier: Modifier = Modifier,
    emoji: String,
    count: String,
    label: String,
    accentColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(accentColor.copy(alpha = 0.18f), AppColors.Surface)
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(count, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            Text(label, fontSize = 11.sp, color = AppColors.TextSecondary, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun ActivityRow(activity: RecentActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mini thumbnail placeholder
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(activity.accentColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(activity.emoji, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(activity.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Spacer(modifier = Modifier.height(3.dp))
            Text(activity.description, fontSize = 12.sp, color = AppColors.TextSecondary, maxLines = 2, lineHeight = 16.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(activity.timeAgo, fontSize = 12.sp, color = AppColors.TextDisabled)
    }
}
