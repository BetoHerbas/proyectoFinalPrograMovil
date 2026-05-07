package com.ucb.proyectofinal.lists.domain.model.vo

data class ListName(val value: String) {
    init {
        require(value.isNotBlank()) { "List name cannot be blank" }
        require(value.length <= 50) { "List name cannot exceed 50 characters" }
    }

    companion object {
        fun of(value: String): Result<ListName> = runCatching { ListName(value) }
    }
}
