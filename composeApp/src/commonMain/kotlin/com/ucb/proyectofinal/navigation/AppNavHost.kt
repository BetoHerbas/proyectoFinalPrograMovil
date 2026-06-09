package com.ucb.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ucb.proyectofinal.auth.presentation.screen.LoginScreen
import com.ucb.proyectofinal.auth.presentation.screen.RegisterScreen
import com.ucb.proyectofinal.explore.presentation.screen.ExploreScreen
import com.ucb.proyectofinal.favorites.presentation.screen.FavoritesScreen
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.presentation.screen.AddItemScreen
import com.ucb.proyectofinal.lists.presentation.screen.ContentListsScreen
import com.ucb.proyectofinal.lists.presentation.screen.CreateListScreen
import com.ucb.proyectofinal.lists.presentation.screen.EditListScreen
import com.ucb.proyectofinal.lists.presentation.screen.ItemDetailScreen
import com.ucb.proyectofinal.lists.presentation.screen.ListDetailScreen
import com.ucb.proyectofinal.onboarding.data.local.OnboardingPreferences
import com.ucb.proyectofinal.onboarding.presentation.screen.OnboardingScreen
import com.ucb.proyectofinal.profile.presentation.screen.ProfileScreen
import com.ucb.proyectofinal.settings.presentation.screen.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun AppNavHost() {
    val onboardingPreferences: OnboardingPreferences = koinInject()
    val startDestination: NavRoute = remember {
        if (onboardingPreferences.isOnboardingCompleted()) NavRoute.Login else NavRoute.Onboarding
    }
    val navController = rememberNavController()

    fun navigateToMainTab(route: NavRoute) {
        navController.navigate(route) {
            popUpTo(NavRoute.ContentLists) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable<NavRoute.Onboarding> {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRoute.Login) {
                        popUpTo<NavRoute.Onboarding> { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoute.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoute.ContentLists) {
                        popUpTo<NavRoute.Login> { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoute.Register) }
            )
        }

        composable<NavRoute.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavRoute.ContentLists) {
                        popUpTo<NavRoute.Register> { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable<NavRoute.ContentLists> {
            ContentListsScreen(
                onNavigateToDetail = { listId, listName, listType, description, coverImageUrl, isPublic ->
                    navController.navigate(
                        NavRoute.ListDetail(
                            listId = listId,
                            listName = listName,
                            listType = listType.name,
                            description = description,
                            coverImageUrl = coverImageUrl,
                            isPublic = isPublic
                        )
                    )
                },
                onNavigateToCreate = { navController.navigate(NavRoute.CreateList) },
                onNavigateToProfile = { navController.navigate(NavRoute.Profile) },
                onNavigateToSettings = { navigateToMainTab(NavRoute.Settings) },
                onNavigateToExplore = { navigateToMainTab(NavRoute.Explore) },
                onNavigateToFavorites = { navigateToMainTab(NavRoute.Favorites) }
            )
        }

        composable<NavRoute.Explore> {
            ExploreScreen(
                onNavigateToDetail = { listId, listName, listType, description, coverImageUrl, isPublic ->
                    navController.navigate(
                        NavRoute.ListDetail(
                            listId = listId,
                            listName = listName,
                            listType = listType.name,
                            description = description,
                            coverImageUrl = coverImageUrl,
                            isPublic = isPublic
                        )
                    )
                },
                onNavigateToHome = { navigateToMainTab(NavRoute.ContentLists) },
                onNavigateToCreate = { navController.navigate(NavRoute.CreateList) },
                onNavigateToFavorites = { navigateToMainTab(NavRoute.Favorites) },
                onNavigateToSettings = { navigateToMainTab(NavRoute.Settings) }
            )
        }

        composable<NavRoute.Favorites> {
            FavoritesScreen(
                onNavigateToHome = { navigateToMainTab(NavRoute.ContentLists) },
                onNavigateToExplore = { navigateToMainTab(NavRoute.Explore) },
                onNavigateToCreate = { navController.navigate(NavRoute.CreateList) },
                onNavigateToSettings = { navigateToMainTab(NavRoute.Settings) },
                onNavigateToDetail = { listId, listName, listType, description, coverImageUrl, isPublic ->
                    navController.navigate(
                        NavRoute.ListDetail(
                            listId = listId,
                            listName = listName,
                            listType = listType.name,
                            description = description,
                            coverImageUrl = coverImageUrl,
                            isPublic = isPublic
                        )
                    )
                }
            )
        }

        composable<NavRoute.ListDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.ListDetail>()
            ListDetailScreen(
                listId = route.listId,
                listName = route.listName,
                listType = route.listType,
                description = route.description,
                coverImageUrl = route.coverImageUrl,
                isPublic = route.isPublic,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddItem = {
                    navController.navigate(
                        NavRoute.AddItem(
                            listId = route.listId,
                            listName = route.listName,
                            listType = route.listType
                        )
                    )
                },
                onNavigateToEdit = {
                    navController.navigate(
                        NavRoute.EditList(
                            listId = route.listId,
                            listName = route.listName,
                            description = route.description,
                            coverImageUrl = route.coverImageUrl,
                            isPublic = route.isPublic,
                            listType = route.listType
                        )
                    )
                },
                onNavigateToItemDetail = { itemId ->
                    navController.navigate(
                        NavRoute.ItemDetail(
                            listId = route.listId,
                            itemId = itemId
                        )
                    )
                }
            )
        }

        composable<NavRoute.ItemDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.ItemDetail>()
            ItemDetailScreen(
                listId = route.listId,
                itemId = route.itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoute.CreateList> {
            CreateListScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<NavRoute.EditList> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.EditList>()
            EditListScreen(
                listId = route.listId,
                listName = route.listName,
                description = route.description,
                coverImageUrl = route.coverImageUrl,
                isPublic = route.isPublic,
                listType = route.listType,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoute.AddItem> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.AddItem>()
            AddItemScreen(
                listId = route.listId,
                listName = route.listName,
                listType = runCatching { ContentType.valueOf(route.listType) }
                    .getOrDefault(ContentType.MOVIE),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoute.Profile> {
            ProfileScreen(
                onLogout = {
                    navController.navigate(NavRoute.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoute.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navigateToMainTab(NavRoute.ContentLists) },
                onNavigateToExplore = { navigateToMainTab(NavRoute.Explore) },
                onNavigateToCreate = { navController.navigate(NavRoute.CreateList) },
                onNavigateToFavorites = { navigateToMainTab(NavRoute.Favorites) }
            )
        }
    }
}
