package com.ucb.proyectofinal.lists.presentation.state

sealed class ItemDetailIntent {
    data class LoadDetail(val itemId: String, val itemType: String) : ItemDetailIntent()
    data object Refresh : ItemDetailIntent()
    data object WatchTrailer : ItemDetailIntent()
    data object AddToWatchlist : ItemDetailIntent()
}
