package com.ucb.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.proyectofinal.ui.auth.LoginScreen
import com.ucb.proyectofinal.ui.auth.RegisterScreen
import com.ucb.proyectofinal.ui.lists.AddItemScreen
import com.ucb.proyectofinal.ui.lists.ContentListsScreen
import com.ucb.proyectofinal.ui.lists.CreateListScreen
import com.ucb.proyectofinal.ui.lists.ListDetailScreen
import com.ucb.proyectofinal.ui.profile.ProfileScreen
import com.ucb.proyectofinal.ui.settings.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.ContentLists) {
        composable<NavRoute.Register> {
            RegisterScreen()
        }

        composable<NavRoute.Login> {
            LoginScreen()
        }

        composable<NavRoute.Profile> {
            ProfileScreen()
        }

        composable<NavRoute.ContentLists> {
            ContentListsScreen(
                onNavigateToAdd = { navController.navigate(NavRoute.AddItem) }
            )
        }

        composable<NavRoute.Settings> {
            SettingsScreen()
        }

        composable<NavRoute.ListDetail> {
            ListDetailScreen()
        }

        composable<NavRoute.CreateList> {
            CreateListScreen()
        }

        composable<NavRoute.AddItem> {
            AddItemScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

