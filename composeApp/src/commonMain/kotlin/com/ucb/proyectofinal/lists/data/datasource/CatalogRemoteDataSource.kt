package com.ucb.proyectofinal.lists.data.datasource

import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.ItemDetail

interface CatalogRemoteDataSource {
    suspend fun searchCatalog(type: ContentType, query: String): List<CatalogSearchItem>
    suspend fun topTenFor(type: ContentType): List<CatalogSearchItem>
    suspend fun getItemDetails(type: ContentType, title: String): ItemDetail
}
