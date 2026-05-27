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

    // Catálogo extendido para asegurar detalles reales según el ítem
    private val movieCatalog = mapOf(
        "dune" to MovieMockData(
            title = "Dune: Part One",
            year = "2021",
            duration = "2h 35m",
            imageUrl = "https://image.tmdb.org/t/p/original/d5NXSklfzfsTMBsLY2iP4M8Iazp.jpg",
            description = "Paul Atreides, a brilliant and gifted young man born into a great destiny beyond his understanding, must travel to the most dangerous planet in the universe to ensure the future of his family and his people.",
            rating = 8.9,
            director = "Denis Villeneuve",
            tags = listOf("Oscar Winner", "Best Picture Nominee"),
            classification = "PG-13",
            guideDetails = listOf("Sequences of strong violence", "Some disturbing images", "Suggestive material"),
            cast = listOf(
                CastMember("Timothée Chalamet", "Paul Atreides", "https://image.tmdb.org/t/p/w200/BE7977No9YpS68pY9pS68pY9pS6.jpg"),
                CastMember("Zendaya", "Chani", null),
                CastMember("Oscar Isaac", "Duke Leto Atreides", null)
            )
        ),
        "interstellar" to MovieMockData(
            title = "Interstellar",
            year = "2014",
            duration = "2h 49m",
            imageUrl = "https://image.tmdb.org/t/p/original/gEU2QniE6E77NI6vCU679yvBPu9.jpg",
            description = "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
            rating = 8.7,
            director = "Christopher Nolan",
            tags = listOf("Oscar Winner", "Visual Masterpiece"),
            classification = "PG-13",
            guideDetails = listOf("Intense peril", "Brief strong language"),
            cast = listOf(
                CastMember("Matthew McConaughey", "Cooper", null),
                CastMember("Anne Hathaway", "Brand", null),
                CastMember("Jessica Chastain", "Murph", null)
            )
        ),
        "last of us" to MovieMockData(
            title = "The Last of Us",
            year = "2023",
            duration = "9 Episodes",
            imageUrl = "https://image.tmdb.org/t/p/original/uKvH56B29VPKn9bRjuo1HqY2Hdm.jpg",
            description = "After a global pandemic destroys civilization, a hardened survivor takes charge of a 14-year-old girl who may be humanity's last hope.",
            rating = 8.8,
            director = "Craig Mazin",
            tags = listOf("Emmy Winner", "Best Adaptation"),
            classification = "TV-MA",
            guideDetails = listOf("Graphic violence", "Strong language"),
            cast = listOf(
                CastMember("Pedro Pascal", "Joel Miller", null),
                CastMember("Bella Ramsey", "Ellie Williams", null)
            )
        ),
        "batman" to MovieMockData(
            title = "The Batman",
            year = "2022",
            duration = "2h 56m",
            imageUrl = "https://image.tmdb.org/t/p/original/74xTEgt7R36Fpooo50r9T6f4uC3.jpg",
            description = "When a sadistic serial killer begins murdering key political figures in Gotham, Batman is forced to investigate the city's hidden corruption and question his family's involvement.",
            rating = 7.8,
            director = "Matt Reeves",
            tags = listOf("Visual Masterpiece", "Best Cinematography"),
            classification = "PG-13",
            guideDetails = listOf("Strong violent content", "Drug content", "Strong language"),
            cast = listOf(
                CastMember("Robert Pattinson", "Bruce Wayne / Batman", null),
                CastMember("Zoë Kravitz", "Selina Kyle / Catwoman", null)
            )
        ),
        "inception" to MovieMockData(
            title = "Inception",
            year = "2010",
            duration = "2h 28m",
            imageUrl = "https://image.tmdb.org/t/p/original/edvWebvCEuV2qrqGZ6uY0M6G16z.jpg",
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            rating = 8.8,
            director = "Christopher Nolan",
            tags = listOf("Oscar Winner", "Best Visual Effects"),
            classification = "PG-13",
            guideDetails = listOf("Sequences of violence and action", "Some disturbing images"),
            cast = listOf(
                CastMember("Leonardo DiCaprio", "Cobb", null),
                CastMember("Joseph Gordon-Levitt", "Arthur", null)
            )
        )
    )

    private data class MovieMockData(
        val title: String,
        val year: String,
        val duration: String,
        val imageUrl: String,
        val description: String,
        val rating: Double,
        val director: String,
        val tags: List<String>,
        val classification: String,
        val guideDetails: List<String>,
        val cast: List<CastMember>
    )

    fun onIntent(intent: ItemDetailIntent) {
        when (intent) {
            is ItemDetailIntent.LoadDetail -> loadItemDetail(intent.itemId)
            ItemDetailIntent.Refresh -> _state.value.item?.id?.value?.let { loadItemDetail(it) }
            else -> {}
        }
    }

    private fun loadItemDetail(itemId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500)

            // Intentamos buscar por coincidencia en el catálogo o usamos un fallback dinámico
            val entry = movieCatalog.entries.find { 
                itemId.contains(it.key, ignoreCase = true) || it.key.contains(itemId, ignoreCase = true) 
            }
            
            val data = entry?.value ?: movieCatalog["dune"]!!
            val finalTitle = if (entry != null) data.title else itemId

            val mockItem = ItemDetail.Movie(
                id = ItemId(itemId),
                title = ItemTitle.of(finalTitle).getOrThrow(),
                description = data.description,
                imageUrl = data.imageUrl,
                rating = data.rating,
                totalReviews = 1200000,
                tags = data.tags,
                parentsGuide = ParentsGuide(data.classification, data.guideDetails),
                cast = data.cast,
                reviews = listOf(
                    Review("Cinephile99", "Absolutely stunning visual effects and story.", 9, "2 days ago"),
                    Review("MovieBuff", "A true masterpiece of our time.", 10, "1 week ago")
                ),
                director = data.director,
                duration = data.duration,
                genres = listOf("Sci-Fi", "Drama", "Action"),
                year = data.year
            )

            _state.update { it.copy(isLoading = false, item = mockItem) }
        }
    }
}
