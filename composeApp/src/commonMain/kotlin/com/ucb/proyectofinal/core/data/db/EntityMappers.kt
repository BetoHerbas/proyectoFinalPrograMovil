package com.ucb.proyectofinal.core.data.db

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.model.vo.Rating

fun ContentListEntity.toDomain(): ContentList = ContentList(
    id = ListId(id),
    name = ListName(name),
    type = ContentType.valueOf(type),
    itemCount = itemCount
)

fun ContentList.toEntity(): ContentListEntity = ContentListEntity(
    id = id.value,
    name = name.value,
    type = type.name,
    itemCount = itemCount
)

fun ContentItemEntity.toDomain(): ContentItem = ContentItem(
    id = ItemId(id),
    listId = ListId(listId),
    title = ItemTitle(title),
    type = ContentType.valueOf(type),
    seen = seen,
    rating = rating?.let { Rating(it) }
)

fun ContentItem.toEntity(): ContentItemEntity = ContentItemEntity(
    id = id.value,
    listId = listId.value,
    title = title.value,
    type = type.name,
    seen = seen,
    rating = rating?.value
)
