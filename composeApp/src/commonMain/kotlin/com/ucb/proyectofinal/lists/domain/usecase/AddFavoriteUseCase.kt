package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class AddFavoriteUseCase(
    private val repository: ContentListRepository
) {
    suspend operator fun invoke(list: ContentList) {
        repository.addFavorite(list)
    }
}
