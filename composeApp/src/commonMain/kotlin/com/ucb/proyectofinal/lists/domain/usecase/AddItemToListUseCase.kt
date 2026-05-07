package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class AddItemToListUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(
        listId: ListId,
        title: ItemTitle,
        type: ContentType
    ): Result<ContentItem> = repository.addItem(listId, title, type)
}
