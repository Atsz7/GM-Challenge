package com.atsz7.ram.hub.data.repositories

import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.daos.FavoritesDao
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.local.entities.FavoriteEntity
import com.atsz7.ram.hub.data.paging.CharacterRemoteMediator
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CharactersRepositoryImplTest {

    private lateinit var repository: CharactersRepositoryImpl
    private val charactersDao: CharactersDao = mockk()
    private val favoritesDao: FavoritesDao = mockk(relaxed = true)
    private val remoteMediator: CharacterRemoteMediator = mockk(relaxed = true)

    @Before
    fun setUp() {
        repository = CharactersRepositoryImpl(charactersDao, favoritesDao, remoteMediator)
    }

    @Test
    fun `requestForceRefresh sets remoteMediator forceRefresh to true`() {

        // When
        repository.requestForceRefresh()

        // Then
        verify { remoteMediator.forceRefresh = true }
    }

    @Test
    fun `getCharacterById maps the DAO's entity to a domain Character`() = runTest {

        // Given
        val characterEntity = CharacterEntity(
            id = 1,
            name = "Rick Sanchez",
            nameNormalized = "rick sanchez",
            status = "Alive",
            specie = "Human",
            type = "",
            gender = "Male",
            originName = "Earth",
            locationName = "Earth",
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            created = "2017-11-04T18:48:46.250Z"
        )
        every { charactersDao.getCharacterById(1) } returns flowOf(characterEntity)

        // When
        val result = repository.getCharacterById(1).first()

        // Then
        assertEquals("Rick Sanchez", result?.name)
    }

    @Test
    fun `getCharacterById emits null when the DAO has no matching entity`() = runTest {

        // Given
        every { charactersDao.getCharacterById(1) } returns flowOf(null)

        // When
        val result = repository.getCharacterById(1).first()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `observeFavoriteIds maps the DAO's favorite ids to a set`() = runTest {

        // Given
        every { favoritesDao.observeFavoriteIds() } returns flowOf(listOf(1, 2))

        // When
        val result = repository.observeFavoriteIds().first()

        // Then
        assertEquals(setOf(1, 2), result)
    }

    @Test
    fun `toggleFavorite with isFavorite true adds a favorite`() = runTest {

        // When
        repository.toggleFavorite(id = 1, isFavorite = true)

        // Then
        coVerify { favoritesDao.add(FavoriteEntity(characterId = 1)) }
    }

    @Test
    fun `toggleFavorite with isFavorite false removes the favorite`() = runTest {

        // When
        repository.toggleFavorite(id = 1, isFavorite = false)

        // Then
        coVerify { favoritesDao.remove(1) }
    }
}
