package com.ucb.proyectofinal.lists.presentation.intent

import com.ucb.proyectofinal.lists.domain.model.ContentType

sealed class ContentListsIntent {
    data object LoadLists : ContentListsIntent()
    data class CreateList(
        val name: String,
        val type: ContentType,
        val description: String = "",
        val coverImageUrl: String? = null,
        val isPublic: Boolean = true
    ) : ContentListsIntent()
    data class DeleteList(val listId: String) : ContentListsIntent()
    data class NavigateToDetail(val listId: String, val listName: String) : ContentListsIntent()
}
