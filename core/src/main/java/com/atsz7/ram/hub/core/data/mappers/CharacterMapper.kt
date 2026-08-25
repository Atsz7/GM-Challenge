package com.atsz7.ram.hub.core.data.mappers

import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.remote.dto.CharacterResult
import com.atsz7.ram.hub.core.domain.model.Character

fun CharacterResult.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status,
    specie = specie,
    type = type,
    gender = gender,
    originName = origin.name,
    locationName = location.name,
    image = image
)

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    specie = specie,
    gender = gender,
    originName = originName,
    locationName = locationName,
    imageUrl = image
)
