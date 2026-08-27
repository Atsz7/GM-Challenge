package com.atsz7.ram.hub.data.mappers

import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterEntityMapperTest {

    @Test
    fun `toDomain maps every field from the local entity`() {

        // Given
        val entity = CharacterEntity(
            id = 1,
            name = "Rick Sanchez",
            nameNormalized = "rick sanchez",
            status = "Alive",
            specie = "Human",
            type = "",
            gender = "Male",
            originName = "Earth (C-137)",
            locationName = "Citadel of Ricks",
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            created = "2017-11-04T18:48:46.250Z"
        )

        // When
        val character = entity.toDomain()

        // Then
        assertEquals(1, character.id)
        assertEquals("Rick Sanchez", character.name)
        assertEquals("Alive", character.status)
        assertEquals("Human", character.specie)
        assertEquals("Male", character.gender)
        assertEquals("Earth (C-137)", character.originName)
        assertEquals("Citadel of Ricks", character.locationName)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", character.imageUrl)
        assertEquals("2017-11-04T18:48:46.250Z", character.createdAt)
    }
}
