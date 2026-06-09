package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.*
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.usecase.FetchItemDetailUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetListItemsUseCase
import com.ucb.proyectofinal.lists.presentation.intent.ItemDetailIntent
import com.ucb.proyectofinal.lists.presentation.state.ItemDetailUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val getListItemsUseCase: GetListItemsUseCase,
    private val fetchItemDetailUseCase: FetchItemDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailUiState())
    val state: StateFlow<ItemDetailUiState> = _state.asStateFlow()

    private val movieCatalog = mapOf(
        "project hail mary" to MovieMockData(
            title = "Project Hail Mary",
            year = "2026",
            duration = "2h 45m",
            director = "Phil Lord & Christopher Miller",
            imageUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?q=80&w=1080&auto=format&fit=crop",
            description = "Ryland Grace es el único superviviente en una misión desesperada. Deberá usar la ciencia y un aliado inesperado para salvar a la humanidad.",
            rating = 9.2,
            tags = listOf("Sci-Fi", "Space", "NASA"),
            cast = listOf(CastMember("Ryan Gosling", "Ryland Grace", null))
        ),
        "the super mario galaxy movie" to MovieMockData(
            title = "The Super Mario Galaxy Movie",
            year = "2026",
            duration = "1h 45m",
            director = "Aaron Horvath",
            imageUrl = "https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?q=80&w=1080&auto=format&fit=crop",
            description = "Mario se embarca en una aventura intergaláctica a través de mundos asombrosos para salvar el cosmos.",
            rating = 8.5,
            tags = listOf("Animation", "Adventure", "Nintendo"),
            cast = listOf(CastMember("Chris Pratt", "Mario", null))
        ),
        "hoppers" to MovieMockData(
            title = "Hoppers",
            year = "2026",
            duration = "1h 35m",
            director = "Daniel Chong",
            imageUrl = "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=1080&auto=format&fit=crop",
            description = "Una joven transfiere su mente al cuerpo de un castor robótico para una misión secreta.",
            rating = 7.8,
            tags = listOf("Pixar", "Comedy", "Sci-Fi"),
            cast = listOf(CastMember("Jon Hamm", "Mayor Bob", null))
        ),
        "over your dead body" to MovieMockData(
            title = "Over Your Dead Body",
            year = "2025",
            duration = "1h 50m",
            director = "Takashi Miike",
            imageUrl = "https://images.unsplash.com/photo-1509248961158-e54f6934749c?q=80&w=1080&auto=format&fit=crop",
            description = "Un thriller sobrenatural donde los límites de la realidad se vuelven sangrientos.",
            rating = 7.4,
            tags = listOf("Horror", "Thriller", "Japan"),
            cast = listOf(CastMember("Ebizo Ichikawa", "Kosuke", null))
        ),
        "lee cronins the mummy" to MovieMockData(
            title = "Lee Cronins the Mummy",
            year = "2025",
            duration = "2h 05m",
            director = "Lee Cronin",
            imageUrl = "https://images.unsplash.com/photo-1605806616949-1e87b487fc2f?q=80&w=1080&auto=format&fit=crop",
            description = "Una nueva visión del terror clásico, enfocada en una maldición antigua y visceral.",
            rating = 7.1,
            tags = listOf("Horror", "Action", "Supernatural"),
            cast = listOf(CastMember("Lee Cronin", "Director", null))
        ),
        "fuze" to MovieMockData(
            title = "Fuze",
            year = "2025",
            duration = "1h 55m",
            director = "David Ayer",
            imageUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=1080&auto=format&fit=crop",
            description = "Un atraco se complica cuando una bomba de la Segunda Guerra Mundial es descubierta.",
            rating = 8.0,
            tags = listOf("Action", "Crime", "Thriller"),
            cast = listOf(CastMember("Aaron Taylor-Johnson", "Lead", null))
        ),
        "avatar: fire and ash" to MovieMockData(
            title = "Avatar: Fire and Ash",
            year = "2025",
            duration = "3h 10m",
            director = "James Cameron",
            imageUrl = "https://images.unsplash.com/photo-1464802686167-b939a67a06d1?q=80&w=1080&auto=format&fit=crop",
            description = "Jake Sully y su familia encuentran una nueva amenaza en la tribu de las cenizas.",
            rating = 9.5,
            tags = listOf("Sci-Fi", "Epic", "Adventure"),
            cast = listOf(CastMember("Sam Worthington", "Jake", null))
        ),
        "the housemaid" to MovieMockData(
            title = "The Housemaid",
            year = "2025",
            duration = "1h 58m",
            director = "Paul Feig",
            imageUrl = "https://images.unsplash.com/photo-1585647347384-2593bc35786b?q=80&w=1080&auto=format&fit=crop",
            description = "Secretos oscuros salen a la luz cuando una joven entra a trabajar en una mansión.",
            rating = 8.2,
            tags = listOf("Mystery", "Drama", "Suspense"),
            cast = listOf(CastMember("Sydney Sweeney", "Maid", null))
        ),
        "the drama" to MovieMockData(
            title = "The Drama",
            year = "2025",
            duration = "2h 15m",
            director = "Kristoffer Borgli",
            imageUrl = "https://images.unsplash.com/photo-1518107616385-ad30891d294e?q=80&w=1080&auto=format&fit=crop",
            description = "Un romance se convierte en pesadilla psicológica antes de una boda.",
            rating = 8.7,
            tags = listOf("A24", "Drama", "Psychological"),
            cast = listOf(CastMember("Zendaya", "Bride", null))
        ),
        "good luck, have fun, dont die" to MovieMockData(
            title = "Good Luck, Have Fun, Dont Die",
            year = "2025",
            duration = "1h 48m",
            director = "Gore Verbinski",
            imageUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1080&auto=format&fit=crop",
            description = "Un viajero del tiempo recluta a un grupo para salvar el futuro.",
            rating = 8.4,
            tags = listOf("Sci-Fi", "Comedy", "Action"),
            cast = listOf(CastMember("Sam Rockwell", "Recruiter", null))
        )
    )

    private data class MovieMockData(
        val title: String, val year: String, val duration: String, val director: String,
        val imageUrl: String, val description: String, val rating: Double, val tags: List<String>, val cast: List<CastMember>
    )

    fun onIntent(intent: ItemDetailIntent) {
        when (intent) {
            is ItemDetailIntent.LoadDetail -> loadItemDetail(intent.itemId, intent.listId)
            else -> {}
        }
    }

    private fun loadItemDetail(itemId: String, listId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(300)

            val items = getListItemsUseCase(ListId(listId)).first()
            val foundItem = items.find { it.id.value == itemId }

            val title = foundItem?.title?.value ?: itemId
            val imageUrl = foundItem?.imageUrl
            val sourceId = foundItem?.sourceId
            val contentType = foundItem?.type ?: ContentType.MOVIE

            // Intentar obtener datos reales desde la API
            val apiData: ItemDetailSourceData? = if (sourceId != null) {
                fetchItemDetailUseCase(sourceId, contentType)
                    .getOrNull()
            } else null

            if (apiData != null) {
                // Construir ItemDetail con datos reales de la API
                val item = buildFromApiData(
                    foundItem = foundItem,
                    sourceData = apiData,
                    title = title,
                    imageUrl = imageUrl,
                    itemId = itemId,
                    contentType = contentType
                )
                _state.update { it.copy(isLoading = false, item = item) }
            } else {
                // Fallback: mock data
                val fallback = buildFallbackItem(
                    foundItem = foundItem,
                    title = title,
                    imageUrl = imageUrl,
                    itemId = itemId,
                    contentType = contentType
                )
                _state.update { it.copy(isLoading = false, item = fallback) }
            }
        }
    }

    private fun buildFromApiData(
        foundItem: ContentItem?,
        sourceData: ItemDetailSourceData,
        title: String,
        imageUrl: String?,
        itemId: String,
        contentType: ContentType
    ): ItemDetail {
        val id = ItemId(foundItem?.id?.value ?: itemId)
        val itemTitle = ItemTitle.of(title).getOrThrow()

        return when (contentType) {
            ContentType.MOVIE -> ItemDetail.Movie(
                id = id,
                title = itemTitle,
                description = sourceData.description,
                imageUrl = imageUrl ?: sourceData.director?.let { null },
                rating = sourceData.rating,
                totalReviews = sourceData.totalReviews,
                tags = sourceData.tags,
                parentsGuide = ParentsGuide("PG-13", listOf("Acción", "Temas maduros")),
                cast = sourceData.cast,
                reviews = sourceData.reviews,
                director = sourceData.director ?: "",
                duration = sourceData.duration ?: "",
                genres = sourceData.genres,
                year = sourceData.year ?: ""
            )
            ContentType.SERIES -> ItemDetail.Series(
                id = id,
                title = itemTitle,
                description = sourceData.description,
                imageUrl = imageUrl,
                rating = sourceData.rating,
                totalReviews = sourceData.totalReviews,
                tags = sourceData.tags,
                parentsGuide = ParentsGuide("PG-13", listOf("Acción", "Temas maduros")),
                cast = sourceData.cast,
                reviews = sourceData.reviews,
                creator = sourceData.creator ?: "",
                episodes = 0,
                seasons = sourceData.seasons ?: 1,
                year = sourceData.year ?: ""
            )
            ContentType.BOOK -> ItemDetail.Book(
                id = id,
                title = itemTitle,
                description = sourceData.description,
                imageUrl = imageUrl,
                rating = sourceData.rating,
                totalReviews = sourceData.totalReviews,
                tags = sourceData.tags,
                parentsGuide = null,
                cast = sourceData.cast,
                reviews = sourceData.reviews,
                author = sourceData.author ?: "",
                pages = sourceData.pages ?: 0,
                publisher = sourceData.publisher ?: ""
            )
            else -> buildFallbackItem(foundItem, title, imageUrl, itemId, contentType)
        }
    }

    private fun buildFallbackItem(
        foundItem: ContentItem?,
        title: String,
        imageUrl: String?,
        itemId: String,
        contentType: ContentType
    ): ItemDetail {
        val searchKey = title.lowercase().trim()
        val entry = movieCatalog.entries.find { (key, _) ->
            searchKey.contains(key) || key.contains(searchKey)
        }

        val data = entry?.value ?: movieCatalog["avatar: fire and ash"]!!
        val mockTitle = if (entry != null) data.title else title

        return ItemDetail.Movie(
            id = ItemId(foundItem?.id?.value ?: itemId),
            title = ItemTitle.of(foundItem?.title?.value ?: mockTitle).getOrThrow(),
            description = data.description,
            imageUrl = imageUrl ?: data.imageUrl,
            rating = data.rating,
            totalReviews = (100..999).random() * 100,
            tags = data.tags,
            parentsGuide = ParentsGuide("PG-13", listOf("Acción", "Temas maduros")),
            cast = data.cast,
            reviews = listOf(Review("User", "Increíble película.", 9, "Hoy")),
            director = data.director,
            duration = data.duration,
            genres = data.tags,
            year = data.year
        )
    }
}
