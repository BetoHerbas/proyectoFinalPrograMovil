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

    NavHost(navController = navController, startDestination = NavRoute.Login) {

        composable<NavRoute.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(NavRoute.Register) },
                onLoginSuccess = {
                    navController.navigate(NavRoute.ContentLists) {
                        popUpTo(NavRoute.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoute.Register> {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(NavRoute.ContentLists) {
                        popUpTo(NavRoute.Register) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoute.ContentLists> {
            ContentListsScreen(
                onListClick = { navController.navigate(NavRoute.ListDetail) },
                onCreateList = { navController.navigate(NavRoute.CreateList) },
                onNavigateToProfile = { navController.navigate(NavRoute.Profile) }
            )
        }

        composable<NavRoute.ListDetail> {
            ListDetailScreen(
                onBack = { navController.popBackStack() },
                onAddItem = { navController.navigate(NavRoute.AddItem) }
            )
        }

        composable<NavRoute.CreateList> {
            CreateListScreen(
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable<NavRoute.AddItem> {
            AddItemScreen(
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable<NavRoute.Profile> {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(NavRoute.Settings) },
                onNavigateToLists = { navController.popBackStack() }
            )
        }

        composable<NavRoute.Settings> {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

