package com.ucb.proyectofinal.auth.domain.model.vo

data class Email(val value: String) {
    init {
        require(value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            "Invalid email format: $value"
        }
    }

    companion object {
        fun of(value: String): Result<Email> = runCatching { Email(value) }
    }
}
