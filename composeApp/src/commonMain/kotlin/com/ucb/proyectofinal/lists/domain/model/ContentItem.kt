package com.ucb.proyectofinal.lists.domain.model

import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.Rating

data class ContentItem(
    val id: ItemId,
    val listId: ListId,
    val title: ItemTitle,
    val type: ContentType,
    val seen: Boolean,
    val rating: Rating?
)
