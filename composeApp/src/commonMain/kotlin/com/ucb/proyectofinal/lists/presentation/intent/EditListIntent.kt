package com.ucb.proyectofinal.lists.presentation.intent

sealed class EditListIntent {
    data class LoadList(
        val listId: String,
        val name: String,
        val description: String,
        val coverImageUrl: String?,
        val isPublic: Boolean,
        val listType: String
    ) : EditListIntent()
    data class UpdateName(val name: String) : EditListIntent()
    data class UpdateDescription(val description: String) : EditListIntent()
    data class TogglePrivate(val isPrivate: Boolean) : EditListIntent()
    data object SaveChanges : EditListIntent()
}
