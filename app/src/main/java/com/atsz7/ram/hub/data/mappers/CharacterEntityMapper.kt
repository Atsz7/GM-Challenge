package com.atsz7.ram.hub.data.mappers

import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.domain.model.Character

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    specie = specie,
    gender = gender,
    originName = originName,
    locationName = locationName,
    imageUrl = image,
    createdAt = created
)
