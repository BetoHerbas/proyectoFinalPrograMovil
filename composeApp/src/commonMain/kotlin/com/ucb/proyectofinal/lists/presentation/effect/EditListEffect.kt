package com.ucb.proyectofinal.lists.presentation.effect

sealed class EditListEffect {
    data object NavigateBack : EditListEffect()
    data class ShowError(val message: String) : EditListEffect()
    data class ShowSuccess(val message: String) : EditListEffect()
}
