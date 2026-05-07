package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class RateItemUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(item: ContentItem, rating: Rating): Result<ContentItem> =
        repository.rateItem(item, rating)
}
