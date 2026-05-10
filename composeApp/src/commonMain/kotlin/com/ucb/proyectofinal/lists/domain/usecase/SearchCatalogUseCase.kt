package com.ucb.proyectofinal.lists.domain.usecase

import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class SearchCatalogUseCase(private val repository: ContentListRepository) {
    suspend operator fun invoke(
        type: ContentType,
        query: String
    ): Result<List<CatalogSearchItem>> = repository.searchCatalog(type, query)
}
