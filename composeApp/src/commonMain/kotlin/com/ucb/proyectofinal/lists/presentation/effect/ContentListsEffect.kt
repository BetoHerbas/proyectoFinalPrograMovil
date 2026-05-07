package com.ucb.proyectofinal.lists.presentation.effect

sealed class ContentListsEffect {
    data class NavigateToDetail(val listId: String, val listName: String) : ContentListsEffect()
    data class ShowError(val message: String) : ContentListsEffect()
    data class ShowSuccess(val message: String) : ContentListsEffect()
}
