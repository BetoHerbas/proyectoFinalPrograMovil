package com.ucb.proyectofinal.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

import org.jetbrains.compose.resources.stringResource
import proyectofinalprogramovil.composeapp.generated.resources.Res
import proyectofinalprogramovil.composeapp.generated.resources.nav_home
import proyectofinalprogramovil.composeapp.generated.resources.nav_explore
import proyectofinalprogramovil.composeapp.generated.resources.nav_add_list
import proyectofinalprogramovil.composeapp.generated.resources.nav_favorites
import proyectofinalprogramovil.composeapp.generated.resources.nav_settings

enum class BottomTab { HOME, EXPLORE, FAVORITES, SETTINGS }

@Composable
fun AppBottomBar(
    currentTab: BottomTab,
    onNavigateToHome: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
            BottomNavItem(
                label = stringResource(Res.string.nav_home),
                icon = Icons.Default.Home,
                selected = currentTab == BottomTab.HOME,
                onClick = onNavigateToHome
            )
            BottomNavItem(
                label = stringResource(Res.string.nav_explore),
                icon = Icons.Default.Search,
                selected = currentTab == BottomTab.EXPLORE,
                onClick = onNavigateToExplore
            )
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Color(0xFF22F2D4),
                contentColor = Color(0xFF043F40),
                modifier = Modifier.size(58.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.nav_add_list))
            }
            BottomNavItem(
                label = stringResource(Res.string.nav_favorites),
                icon = Icons.Default.Favorite,
                selected = currentTab == BottomTab.FAVORITES,
                onClick = onNavigateToFavorites
            )
            BottomNavItem(
                label = stringResource(Res.string.nav_settings),
                icon = Icons.Default.Settings,
                selected = currentTab == BottomTab.SETTINGS,
                onClick = onNavigateToSettings
            )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (selected) Color(0xFF22F2D4) else Color(0xFF7EA8B4)
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (selected) Color(0xFF22F2D4) else Color.Transparent)
            )
            Spacer(Modifier.height(4.dp))
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp))
            }
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
