package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class DeleteItemUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(item: ContentItem): Result<Unit> =
        repository.deleteItem(item)
}
