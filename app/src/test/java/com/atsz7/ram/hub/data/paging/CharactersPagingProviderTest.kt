package com.atsz7.ram.hub.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
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
class CharactersPagingProviderTest {

    private lateinit var provider: CharactersPagingProvider
    private val charactersDao: CharactersDao = mockk()
    private val remoteMediator: CharacterRemoteMediator = mockk(relaxed = true)

    @Before
    fun setUp() = runTest {

        coEvery {
            remoteMediator.initialize()
        } returns RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH

        coEvery {
            remoteMediator.load(any(), any())
        } returns RemoteMediator.MediatorResult.Success(endOfPaginationReached = true)

        provider = CharactersPagingProvider(charactersDao, remoteMediator)
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
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                created = "2017-11-04T18:48:46.250Z"
            )
        )
        val pagingSourceFactory = characterEntities.asPagingSourceFactory()
        every { charactersDao.getAll() } returns pagingSourceFactory()

        // When
        val result = provider.getCharacters().asSnapshot()

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
        provider.getCharacters(query = "  ").asSnapshot()

        // Then
        verify { charactersDao.getAll() }
        verify(exactly = 0) { charactersDao.search(any()) }
    }

    @Test
    fun `getCharacters with a search query uses the local database search and skips remote loading`() =
        runTest {

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
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                    created = "2017-11-04T18:48:46.250Z"
                )
            )
            val pagingSourceFactory = characterEntities.asPagingSourceFactory()
            every { charactersDao.search(any()) } returns pagingSourceFactory()

            // When
            val result = provider.getCharacters(query = "rick").asSnapshot()

            // Then
            assertEquals(1, result.size)
            assertEquals("Rick Sánchez", result[0].name)
            verify { charactersDao.search("rick") }
            coVerify(exactly = 0) { remoteMediator.load(any(), any()) }
        }

    @Test
    fun `getCharacters with favoritesOnly and a blank query uses getFavorites`() = runTest {

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
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                created = "2017-11-04T18:48:46.250Z"
            )
        )
        val pagingSourceFactory = characterEntities.asPagingSourceFactory()
        every { charactersDao.getFavorites() } returns pagingSourceFactory()

        // When
        val result = provider.getCharacters(favoritesOnly = true).asSnapshot()

        // Then
        assertEquals(1, result.size)
        assertEquals("Rick Sanchez", result[0].name)
        verify { charactersDao.getFavorites() }
        coVerify(exactly = 0) { remoteMediator.load(any(), any()) }
    }

    @Test
    fun `getCharacters with favoritesOnly and a search query uses searchFavorites`() = runTest {

        // Given
        val pagingSourceFactory = emptyList<CharacterEntity>().asPagingSourceFactory()
        every { charactersDao.searchFavorites(any()) } returns pagingSourceFactory()

        // When
        provider.getCharacters(query = "rick", favoritesOnly = true).asSnapshot()

        // Then
        verify { charactersDao.searchFavorites("rick") }
        verify(exactly = 0) { charactersDao.getFavorites() }
    }
}
