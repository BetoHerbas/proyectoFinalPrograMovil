package com.ucb.proyectofinal.lists.domain.model

data class ItemDetailSourceData(
    val description: String = "",
    val rating: Double = 0.0,
    val tags: List<String> = emptyList(),
    val cast: List<CastMember> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val parentsGuide: ParentsGuide? = null,
    val director: String? = null,
    val creator: String? = null,
    val duration: String? = null,
    val seasons: Int? = null,
    val year: String? = null,
    val author: String? = null,
    val pages: Int? = null,
    val publisher: String? = null,
    val genres: List<String> = emptyList(),
    val totalReviews: Int = 0
)
