package com.ucb.proyectofinal.lists.presentation.intent

import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType

sealed class AddItemIntent {
    data class Init(
        val listId: String,
        val listName: String,
        val listType: ContentType
    ) : AddItemIntent()

    data class QueryChanged(val query: String) : AddItemIntent()
    data object Search : AddItemIntent()
    data class AddToList(val item: CatalogSearchItem) : AddItemIntent()
}
