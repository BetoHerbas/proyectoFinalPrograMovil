package com.ucb.proyectofinal.lists.domain.model

data class CatalogSearchItem(
    val sourceId: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String? = null
)
