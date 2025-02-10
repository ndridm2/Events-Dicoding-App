package com.ndridm.eventsdicodingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class EventEntity(
    @field:PrimaryKey
    val id: Int? = null,
    val name: String? = null,
    var mediaCover: String? = null,
    var beginTime: String? = null,
)
