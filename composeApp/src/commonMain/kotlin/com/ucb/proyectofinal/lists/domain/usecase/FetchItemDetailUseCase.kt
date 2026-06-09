package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.ItemDetailSourceData
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class FetchItemDetailUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(
        sourceId: String,
        type: ContentType
    ): Result<ItemDetailSourceData> = repository.fetchDetail(sourceId, type)
}
