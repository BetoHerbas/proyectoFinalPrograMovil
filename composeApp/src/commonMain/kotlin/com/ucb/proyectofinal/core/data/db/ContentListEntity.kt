package com.ucb.proyectofinal.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_lists")
data class ContentListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val itemCount: Int = 0,
    val description: String = "",
    val coverImageUrl: String? = null,
    val isPublic: Boolean = true
)
