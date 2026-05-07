package com.ucb.proyectofinal.lists.domain.model

import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName

data class ContentList(
    val id: ListId,
    val name: ListName,
    val type: ContentType,
    val itemCount: Int,
    val description: String = "",
    val coverImageUrl: String? = null,
    val isPublic: Boolean = true
)
