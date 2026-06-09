package com.ucb.proyectofinal.lists.domain.model

import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle

sealed interface ItemDetail {
    val id: ItemId
    val title: ItemTitle
    val description: String
    val imageUrl: String?
    val rating: Double
    val totalReviews: Int
    val tags: List<String>
    val parentsGuide: ParentsGuide?
    val cast: List<CastMember>
    val reviews: List<Review>

    data class Movie(
        override val id: ItemId,
        override val title: ItemTitle,
        override val description: String,
        override val imageUrl: String?,
        override val rating: Double,
        override val totalReviews: Int,
        override val tags: List<String>,
        override val parentsGuide: ParentsGuide?,
        override val cast: List<CastMember>,
        override val reviews: List<Review>,
        val director: String,
        val duration: String,
        val genres: List<String>,
        val year: String
    ) : ItemDetail

    data class Series(
        override val id: ItemId,
        override val title: ItemTitle,
        override val description: String,
        override val imageUrl: String?,
        override val rating: Double,
        override val totalReviews: Int,
        override val tags: List<String>,
        override val parentsGuide: ParentsGuide?,
        override val cast: List<CastMember>,
        override val reviews: List<Review>,
        val creator: String,
        val episodes: Int,
        val seasons: Int,
        val year: String
    ) : ItemDetail

    data class Book(
        override val id: ItemId,
        override val title: ItemTitle,
        override val description: String,
        override val imageUrl: String?,
        override val rating: Double,
        override val totalReviews: Int,
        override val tags: List<String>,
        override val parentsGuide: ParentsGuide?,
        override val cast: List<CastMember>, // Can be used for contributors or empty
        override val reviews: List<Review>,
        val author: String,
        val pages: Int,
        val publisher: String
    ) : ItemDetail
}

data class ParentsGuide(
    val classification: String,
    val details: List<String>
)

data class CastMember(
    val name: String,
    val role: String,
    val imageUrl: String?
)

data class Review(
    val author: String,
    val content: String,
    val rating: Int,
    val date: String
)
