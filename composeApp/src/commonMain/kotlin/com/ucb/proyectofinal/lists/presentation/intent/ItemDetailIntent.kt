package com.ucb.proyectofinal.lists.presentation.intent

sealed class ItemDetailIntent {
    data class LoadDetail(val itemId: String) : ItemDetailIntent()
    data object Refresh : ItemDetailIntent()
    data object WatchTrailer : ItemDetailIntent()
    data object AddToWatchlist : ItemDetailIntent()
}
