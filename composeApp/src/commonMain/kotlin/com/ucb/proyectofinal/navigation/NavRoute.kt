package com.ucb.proyectofinal.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {

    @Serializable
    object Register : NavRoute()

    @Serializable
    object Login : NavRoute()

    @Serializable
    object Profile : NavRoute()

    @Serializable
    object ContentLists : NavRoute()

    @Serializable
    object Settings : NavRoute()

    @Serializable
    object ListDetail : NavRoute()

    @Serializable
    object CreateList : NavRoute()

    @Serializable
    object AddItem : NavRoute()
}
