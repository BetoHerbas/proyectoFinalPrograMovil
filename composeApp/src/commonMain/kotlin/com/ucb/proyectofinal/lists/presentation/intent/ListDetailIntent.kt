package com.ucb.proyectofinal.lists.presentation.intent

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType

sealed class ListDetailIntent {
    data class LoadDetail(val listId: String, val listName: String = "") : ListDetailIntent()
    data class AddItem(val title: String, val type: ContentType) : ListDetailIntent()
    data class ToggleSeen(val item: ContentItem) : ListDetailIntent()
    data class RateItem(val item: ContentItem, val rating: Int) : ListDetailIntent()
    data class DeleteItem(val item: ContentItem) : ListDetailIntent()
    data object ShowAddDialog : ListDetailIntent()
    data object HideAddDialog : ListDetailIntent()
}
