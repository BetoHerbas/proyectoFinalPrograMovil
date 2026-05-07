package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class CreateContentListUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(
        name: ListName,
        type: ContentType,
        description: String = "",
        coverImageUrl: String? = null,
        isPublic: Boolean = true
    ): Result<ContentList> =
        repository.createList(name, type, description, coverImageUrl, isPublic)
}
