package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class RemoveFavoriteUseCase(
    private val repository: ContentListRepository
) {
    suspend operator fun invoke(id: ListId) {
        repository.removeFavorite(id)
    }
}
