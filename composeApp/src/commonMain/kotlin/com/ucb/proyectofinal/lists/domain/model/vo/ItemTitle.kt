package com.ucb.proyectofinal.lists.domain.model.vo

data class ItemTitle(val value: String) {
    init {
        require(value.isNotBlank()) { "Item title cannot be blank" }
        require(value.length <= 100) { "Item title cannot exceed 100 characters" }
    }

    companion object {
        fun of(value: String): Result<ItemTitle> = runCatching { ItemTitle(value) }
    }
}
