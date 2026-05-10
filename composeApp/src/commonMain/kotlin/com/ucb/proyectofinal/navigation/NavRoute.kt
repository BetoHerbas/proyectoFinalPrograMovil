package com.ucb.proyectofinal.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {

    @Serializable
    data object Register : NavRoute()

    @Serializable
    data object Login : NavRoute()

    @Serializable
    data object Profile : NavRoute()

    @Serializable
    data object ContentLists : NavRoute()

    @Serializable
    data object Settings : NavRoute()

    @Serializable
    data class ListDetail(
        val listId: String,
        val listName: String = "",
        val listType: String = "MOVIE"
    ) : NavRoute()

    @Serializable
    data object CreateList : NavRoute()

    @Serializable
    data class AddItem(
        val listId: String,
        val listName: String = "",
        val listType: String = "MOVIE"
    ) : NavRoute()
}
