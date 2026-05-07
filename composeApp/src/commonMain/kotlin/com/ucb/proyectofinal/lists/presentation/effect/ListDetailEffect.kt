package com.ucb.proyectofinal.lists.presentation.effect

sealed class ListDetailEffect {
    data object NavigateBack : ListDetailEffect()
    data class ShowError(val message: String) : ListDetailEffect()
    data class ShowSuccess(val message: String) : ListDetailEffect()
}
