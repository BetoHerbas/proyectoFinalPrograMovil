package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.CastMember
import com.ucb.proyectofinal.lists.domain.model.ItemDetail
import com.ucb.proyectofinal.lists.domain.model.ParentsGuide
import com.ucb.proyectofinal.lists.domain.model.Review
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.presentation.intent.ItemDetailIntent
import com.ucb.proyectofinal.lists.presentation.state.ItemDetailUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailUiState())
    val state: StateFlow<ItemDetailUiState> = _state.asStateFlow()

    fun onIntent(intent: ItemDetailIntent) {
        when (intent) {
            is ItemDetailIntent.LoadDetail -> loadItemDetail(intent.itemId)
            ItemDetailIntent.Refresh -> _state.value.item?.id?.value?.let { loadItemDetail(it) }
            ItemDetailIntent.WatchTrailer -> { /* Handle watch trailer */ }
            ItemDetailIntent.AddToWatchlist -> { /* Handle add to watchlist */ }
        }
    }

    private fun loadItemDetail(itemId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Simulating repository call
            delay(1000)
            
            val mockItem = ItemDetail.Movie(
                id = ItemId(itemId),
                title = ItemTitle.of("Dune: Part One").getOrThrow(),
                description = "A noble family becomes embroiled in a war for control over the galaxy's most valuable asset while its heir becomes troubled by visions of a dark future.",
                imageUrl = "https://image.tmdb.org/t/p/original/d5NXSklfzfsTMBsLY2iP4M8Iazp.jpg",
                rating = 8.9,
                totalReviews = 1200000,
                tags = listOf("Oscar Winner", "Best Picture Nominee"),
                parentsGuide = ParentsGuide(
                    classification = "PG-13",
                    details = listOf(
                        "Sequences of strong violence",
                        "Some disturbing images",
                        "Suggestive material"
                    )
                ),
                cast = listOf(
                    CastMember("Timothée Chalamet", "Paul Atreides", null),
                    CastMember("Zendaya", "Chani", null),
                    CastMember("Oscar Isaac", "Duke Leto", null)
                ),
                reviews = listOf(
                    Review("SciFiMad", "A cinematic masterpiece. Villeneuve has achieved the impossible...", 9, "2 weeks ago")
                ),
                director = "Denis Villeneuve",
                duration = "2h 35m",
                genres = listOf("Sci-Fi", "Adventure"),
                year = "2021"
            )

            _state.update { it.copy(isLoading = false, item = mockItem) }
        }
    }
}
