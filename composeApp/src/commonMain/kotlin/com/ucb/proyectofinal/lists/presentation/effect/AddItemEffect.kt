package com.ucb.proyectofinal.lists.presentation.effect

sealed class AddItemEffect {
    data class ShowError(val message: String) : AddItemEffect()
    data class ShowSuccess(val message: String) : AddItemEffect()
}
