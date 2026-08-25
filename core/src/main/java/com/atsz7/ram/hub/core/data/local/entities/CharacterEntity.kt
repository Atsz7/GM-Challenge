package com.atsz7.ram.hub.core.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    indices = [Index(value = ["nameNormalized"])]
)
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nameNormalized: String,
    val status: String,
    val specie: String,
    val type: String,
    val gender: String,
    val originName: String,
    val locationName: String,
    val image: String
)
