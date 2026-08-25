package com.atsz7.ram.hub.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters_remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val id: Int = 0,
    val nextPage: Int?
)
