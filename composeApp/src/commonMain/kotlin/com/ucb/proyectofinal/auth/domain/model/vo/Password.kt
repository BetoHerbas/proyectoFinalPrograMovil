package com.ucb.proyectofinal.auth.domain.model.vo

data class Password(val value: String) {
    init {
        require(value.length >= 6) { "Password must be at least 6 characters" }
    }

    companion object {
        fun of(value: String): Result<Password> = runCatching { Password(value) }
    }
}
