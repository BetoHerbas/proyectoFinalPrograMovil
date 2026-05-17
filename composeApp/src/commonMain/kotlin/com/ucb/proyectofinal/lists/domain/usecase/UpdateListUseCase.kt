package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class UpdateListUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(
        listId: ListId,
        name: ListName,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ): Result<ContentList> = repository.updateList(
        listId = listId,
        name = name,
        description = description,
        coverImageUrl = coverImageUrl,
        isPublic = isPublic
    )
}
