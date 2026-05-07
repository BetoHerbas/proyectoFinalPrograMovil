package com.ucb.proyectofinal.lists.domain.model.vo

data class Rating(val value: Int) {
    init {
        require(value in 1..5) { "Rating must be between 1 and 5, got $value" }
    }

    companion object {
        fun of(value: Int): Result<Rating> = runCatching { Rating(value) }
    }
}
