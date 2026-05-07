package com.ucb.proyectofinal.lists.domain.model.vo

data class ListId(val value: String) {
    init {
        require(value.isNotBlank()) { "ListId cannot be blank" }
    }

    companion object {
        fun of(value: String): Result<ListId> = runCatching { ListId(value) }
    }
}
