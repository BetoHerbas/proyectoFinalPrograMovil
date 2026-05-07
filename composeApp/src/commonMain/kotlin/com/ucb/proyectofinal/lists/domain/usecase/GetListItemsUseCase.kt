package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import kotlinx.coroutines.flow.Flow

class GetListItemsUseCase(private val repository: ContentListRepository) {
    operator fun invoke(listId: ListId): Flow<List<ContentItem>> =
        repository.getListItems(listId)
}
