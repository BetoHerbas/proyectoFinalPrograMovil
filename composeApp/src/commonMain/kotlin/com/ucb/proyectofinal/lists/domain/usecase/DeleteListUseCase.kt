package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class DeleteListUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(listId: ListId): Result<Unit> =
        repository.deleteList(listId)
}
