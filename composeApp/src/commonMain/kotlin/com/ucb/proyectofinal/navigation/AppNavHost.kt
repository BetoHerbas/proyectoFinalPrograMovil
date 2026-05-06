package com.ucb.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.proyectofinal.auth.presentation.screen.LoginScreen
import com.ucb.proyectofinal.auth.presentation.screen.RegisterScreen
import com.ucb.proyectofinal.lists.presentation.screen.AddItemScreen
import com.ucb.proyectofinal.lists.presentation.screen.ContentListsScreen
import com.ucb.proyectofinal.lists.presentation.screen.CreateListScreen
import com.ucb.proyectofinal.lists.presentation.screen.ListDetailScreen
import com.ucb.proyectofinal.profile.presentation.screen.ProfileScreen
import com.ucb.proyectofinal.settings.presentation.screen.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.Login) {
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
            ContentListsScreen()
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
            AddItemScreen()
        }
    }
}
