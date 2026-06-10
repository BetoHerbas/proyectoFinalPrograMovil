package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(
    private val repository: ContentListRepository
) {
    operator fun invoke(): Flow<List<ContentList>> {
        return repository.getFavorites()
    }
}
