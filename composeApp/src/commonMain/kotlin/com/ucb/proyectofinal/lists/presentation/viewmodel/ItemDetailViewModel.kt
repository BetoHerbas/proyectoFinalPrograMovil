package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.*
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

    private val movieCatalog = mapOf(
        "dune" to MovieMockData(
            title = "Dune: Part One",
            year = "2021",
            duration = "2h 35m",
            director = "Denis Villeneuve",
            imageUrl = "https://image.tmdb.org/t/p/original/d5NXSklfzfsTMBsLY2iP4M8Iazp.jpg",
            description = "Paul Atreides, a brilliant and gifted young man born into a great destiny, must travel to the most dangerous planet in the universe to ensure the future of his family and his people.",
            rating = 8.9,
            tags = listOf("Oscar Winner", "Sci-Fi"),
            cast = listOf(CastMember("Timothée Chalamet", "Paul Atreides", null), CastMember("Zendaya", "Chani", null))
        ),
        "interstellar" to MovieMockData(
            title = "Interstellar",
            year = "2014",
            duration = "2h 49m",
            director = "Christopher Nolan",
            imageUrl = "https://image.tmdb.org/t/p/original/gEU2QniE6E77NI6vCU679yvBPu9.jpg",
            description = "In the future, where Earth is becoming uninhabitable, a farmer and ex-NASA pilot is tasked to pilot a spacecraft to find a new planet for humans.",
            rating = 8.7,
            tags = listOf("Oscar Winner", "Space"),
            cast = listOf(CastMember("Matthew McConaughey", "Cooper", null), CastMember("Anne Hathaway", "Amelia", null))
        ),
        "drama" to MovieMockData(
            title = "The Drama",
            year = "2024",
            duration = "2h 10m",
            director = "Elena G. Ruiz",
            imageUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=1000&auto=format&fit=crop",
            description = "An intense and moving exploration of human resilience and the complex emotional ties that bind us together in times of crisis.",
            rating = 9.4,
            tags = listOf("Top Rated", "Emotional"),
            cast = listOf(CastMember("Meryl Streep", "Matriarch", null), CastMember("Viola Davis", "The Friend", null))
        ),
        "batman" to MovieMockData(
            title = "The Batman",
            year = "2022",
            duration = "2h 56m",
            director = "Matt Reeves",
            imageUrl = "https://image.tmdb.org/t/p/original/74xTEgt7R36Fpooo50r9T6f4uC3.jpg",
            description = "Batman ventures into Gotham City's underworld when a sadistic killer leaves behind a trail of cryptic clues.",
            rating = 7.9,
            tags = listOf("Action", "Detective"),
            cast = listOf(CastMember("Robert Pattinson", "Bruce Wayne", null), CastMember("Zoë Kravitz", "Selina Kyle", null))
        ),
        "last" to MovieMockData(
            title = "The Last of Us",
            year = "2023",
            duration = "9 Episodes",
            director = "Craig Mazin",
            imageUrl = "https://image.tmdb.org/t/p/original/uKvH56B29VPKn9bRjuo1HqY2Hdm.jpg",
            description = "After a global pandemic destroys civilization, a hardened survivor takes charge of a girl who may be humanity's last hope.",
            rating = 8.8,
            tags = listOf("Emmy Winner", "Gaming"),
            cast = listOf(CastMember("Pedro Pascal", "Joel", null), CastMember("Bella Ramsey", "Ellie", null))
        )
    )

    private data class MovieMockData(
        val title: String, val year: String, val duration: String, val director: String,
        val imageUrl: String, val description: String, val rating: Double, val tags: List<String>, val cast: List<CastMember>
    )

    fun onIntent(intent: ItemDetailIntent) {
        when (intent) {
            is ItemDetailIntent.LoadDetail -> loadItemDetail(intent.itemId)
            else -> {}
        }
    }

    private fun loadItemDetail(itemId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500)

            // Buscamos coincidencia en el catálogo (el itemId es el título enviado desde la lista)
            val searchKey = itemId.lowercase().trim()
            val entry = movieCatalog.entries.find { (key, _) -> 
                searchKey.contains(key) || key.contains(searchKey) 
            }
            
            val data = entry?.value ?: movieCatalog["dune"]!!
            
            // Si el itemId no es un ID técnico de Firebase, lo usamos para el título para que el nombre sea correcto
            val isTechnicalId = itemId.startsWith("-") && itemId.length > 10
            val finalTitle = if (entry != null) data.title else if (isTechnicalId) "Detalles de Película" else itemId

            val mockItem = ItemDetail.Movie(
                id = ItemId(itemId),
                title = ItemTitle.of(finalTitle).getOrThrow(),
                description = data.description,
                imageUrl = data.imageUrl,
                rating = data.rating,
                totalReviews = (100..999).random() * 1000,
                tags = data.tags,
                parentsGuide = ParentsGuide("PG-13", listOf("Secuencias de acción", "Temas maduros")),
                cast = data.cast,
                reviews = listOf(Review("Cinephile", "Increíble experiencia.", 9, "2d ago")),
                director = data.director,
                duration = data.duration,
                genres = listOf("Cine", "Drama"),
                year = data.year
            )

            _state.update { it.copy(isLoading = false, item = mockItem) }
        }
    }
}
