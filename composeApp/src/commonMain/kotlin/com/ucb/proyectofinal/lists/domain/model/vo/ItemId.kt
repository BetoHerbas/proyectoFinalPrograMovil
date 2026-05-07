package com.ucb.proyectofinal.lists.domain.model.vo

data class ItemId(val value: String) {
    init {
        require(value.isNotBlank()) { "ItemId cannot be blank" }
    }

    companion object {
        fun of(value: String): Result<ItemId> = runCatching { ItemId(value) }
    }
}
