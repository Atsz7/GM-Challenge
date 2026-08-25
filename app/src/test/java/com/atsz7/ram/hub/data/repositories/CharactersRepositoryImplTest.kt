package com.atsz7.ram.hub.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.paging.CharacterRemoteMediator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class CharactersRepositoryImplTest {

    private lateinit var repository: CharactersRepositoryImpl
    private val database: RamHubDatabase = mockk()
    private val charactersDao: CharactersDao = mockk()
    private val remoteMediator: CharacterRemoteMediator = mockk(relaxed = true)

    @Before
    fun setUp() = runTest {

        every { database.charactersDao() } returns charactersDao

        coEvery {
            remoteMediator.initialize()
        } returns RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH

        coEvery {
            remoteMediator.load(any(), any())
        } returns RemoteMediator.MediatorResult.Success(endOfPaginationReached = true)

        repository = CharactersRepositoryImpl(database, remoteMediator)
    }

    @Test
    fun `requestForceRefresh sets remoteMediator forceRefresh to true`() {

        // When
        repository.requestForceRefresh()

        // Then
        verify { remoteMediator.forceRefresh = true }
    }

    @Test
    fun `getCharacters emits mapped characters from DAO`() = runTest {

        // Given
        val characterEntities = listOf(
            CharacterEntity(
                id = 1,
                name = "Rick Sanchez",
                nameNormalized = "rick sanchez",
                status = "Alive",
                specie = "Human",
                type = "",
                gender = "Male",
                originName = "Earth",
                locationName = "Earth",
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
            )
        )
        val pagingSourceFactory = characterEntities.asPagingSourceFactory()
        every { charactersDao.getAll() } returns pagingSourceFactory()

        // When
        val result = repository.getCharacters().asSnapshot()

        // Then
        assertEquals(1, result.size)
        assertEquals("Rick Sanchez", result[0].name)
    }

    @Test
    fun `getCharacters with a blank query uses getAll and the remote mediator`() = runTest {

        // Given
        val pagingSourceFactory = emptyList<CharacterEntity>().asPagingSourceFactory()
        every { charactersDao.getAll() } returns pagingSourceFactory()

        // When
        repository.getCharacters(query = "  ").asSnapshot()

        // Then
        verify { charactersDao.getAll() }
        verify(exactly = 0) { charactersDao.search(any()) }
    }

    @Test
    fun `getCharacters with a search query uses the local database search and skips remote loading`() = runTest {

        // Given
        val characterEntities = listOf(
            CharacterEntity(
                id = 1,
                name = "Rick Sánchez",
                nameNormalized = "rick sanchez",
                status = "Alive",
                specie = "Human",
                type = "",
                gender = "Male",
                originName = "Earth",
                locationName = "Earth",
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
            )
        )
        val pagingSourceFactory = characterEntities.asPagingSourceFactory()
        every { charactersDao.search(any()) } returns pagingSourceFactory()

        // When
        val result = repository.getCharacters(query = "rick").asSnapshot()

        // Then
        assertEquals(1, result.size)
        assertEquals("Rick Sánchez", result[0].name)
        verify { charactersDao.search("rick") }
        coVerify(exactly = 0) { remoteMediator.load(any(), any()) }
    }
}
