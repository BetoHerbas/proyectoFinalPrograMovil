package com.ucb.proyectofinal.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_items")
data class ContentItemEntity(
    @PrimaryKey val id: String,
    val listId: String,
    val title: String,
    val type: String,
    val seen: Boolean = false,
    val rating: Int? = null
)
