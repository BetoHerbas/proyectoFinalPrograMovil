package com.ucb.proyectofinal.lists.presentation.effect

import com.ucb.proyectofinal.lists.domain.model.ContentType

sealed class ContentListsEffect {
    data class NavigateToDetail(
        val listId: String,
        val listName: String,
        val listType: ContentType
    ) : ContentListsEffect()
    data class ShowError(val message: String) : ContentListsEffect()
    data class ShowSuccess(val message: String) : ContentListsEffect()
}
