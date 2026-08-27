package com.atsz7.ram.hub.domain.model

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val specie: String,
    val gender: String,
    val originName: String,
    val locationName: String,
    val imageUrl: String,
    val createdAt: String
)
