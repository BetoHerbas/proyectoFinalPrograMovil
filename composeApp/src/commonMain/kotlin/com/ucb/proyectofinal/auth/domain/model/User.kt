package com.ucb.proyectofinal.auth.domain.model

import com.ucb.proyectofinal.auth.domain.vo.Email
import com.ucb.proyectofinal.auth.domain.vo.UserId

data class User(
    val id: UserId,
    val email: Email,
    val name: String
)
