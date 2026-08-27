package com.atsz7.ram.hub.core.data.mappers

import com.atsz7.ram.hub.core.data.remote.dto.CharacterPlace
import com.atsz7.ram.hub.core.data.remote.dto.CharacterResult
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterResultMapperTest {

    @Test
    fun `toEntity maps every field from the API result`() {

        // Given
        val result = CharacterResult(
            id = 1,
            name = "Rick Sánchez",
            status = "Alive",
            specie = "Human",
            type = "",
            gender = "Male",
            origin = CharacterPlace(name = "Earth (C-137)", url = "https://origin.url"),
            location = CharacterPlace(name = "Citadel of Ricks", url = "https://location.url"),
            image = "https://image.url",
            url = "https://character.url",
            created = "2017-11-04T18:48:46.250Z"
        )

        // When
        val entity = result.toEntity()

        // Then
        assertEquals(1, entity.id)
        assertEquals("Rick Sánchez", entity.name)
        assertEquals("Alive", entity.status)
        assertEquals("Human", entity.specie)
        assertEquals("", entity.type)
        assertEquals("Male", entity.gender)
        assertEquals("Earth (C-137)", entity.originName)
        assertEquals("Citadel of Ricks", entity.locationName)
        assertEquals("https://image.url", entity.image)
        assertEquals("2017-11-04T18:48:46.250Z", entity.created)
    }

    @Test
    fun `toEntity derives nameNormalized from name`() {

        // Given
        val result = CharacterResult(
            id = 2,
            name = "Rick Sánchez",
            status = "Alive",
            specie = "Human",
            type = "",
            gender = "Male",
            origin = CharacterPlace(name = "Earth (C-137)", url = "https://origin.url"),
            location = CharacterPlace(name = "Citadel of Ricks", url = "https://location.url"),
            image = "https://image.url",
            url = "https://character.url",
            created = "2017-11-04T18:48:46.250Z"
        )

        // When
        val entity = result.toEntity()

        // Then
        assertEquals("rick sanchez", entity.nameNormalized)
    }
}
