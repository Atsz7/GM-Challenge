package com.atsz7.ram.hub.core.data.mappers

import com.atsz7.ram.hub.core.extensions.normalizeForSearch
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.remote.dto.CharacterResult
import com.atsz7.ram.hub.core.domain.model.Character

fun CharacterResult.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    nameNormalized = name.normalizeForSearch(),
    status = status,
    specie = specie,
    type = type,
    gender = gender,
    originName = origin.name,
    locationName = location.name,
    image = image,
    created = created
)

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
