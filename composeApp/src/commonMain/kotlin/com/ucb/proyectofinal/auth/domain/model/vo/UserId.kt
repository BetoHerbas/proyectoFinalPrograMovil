package com.ucb.proyectofinal.auth.domain.model.vo

data class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "UserId cannot be blank" }
    }

    companion object {
        fun of(value: String): Result<UserId> = runCatching { UserId(value) }
    }
}
