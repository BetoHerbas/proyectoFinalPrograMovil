package com.ucb.proyectofinal.lists.presentation.intent

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.presentation.state.ItemFilter

sealed class ListDetailIntent {
    data class LoadDetail(
        val listId: String,
        val listName: String = "",
        val description: String = "",
        val coverImageUrl: String? = null,
        val isPublic: Boolean = true,
        val listType: String = "MOVIE"
    ) : ListDetailIntent()
    data class AddItem(val title: String, val type: ContentType) : ListDetailIntent()
    data class ToggleSeen(val item: ContentItem) : ListDetailIntent()
    data class RateItem(val item: ContentItem, val rating: Int) : ListDetailIntent()
    data class DeleteItem(val item: ContentItem) : ListDetailIntent()
    data object ShowAddDialog : ListDetailIntent()
    data object HideAddDialog : ListDetailIntent()
    data class ChangeFilter(val filter: ItemFilter) : ListDetailIntent()
}
