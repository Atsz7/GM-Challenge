package com.atsz7.ram.hub.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.daos.FavoritesDao
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.local.entities.FavoriteEntity
import com.atsz7.ram.hub.core.data.paging.CharacterRemoteMediator
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
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
    private val charactersDao: CharactersDao = mockk()
    private val favoritesDao: FavoritesDao = mockk(relaxed = true)
    private val remoteMediator: CharacterRemoteMediator = mockk(relaxed = true)

    @Before
    fun setUp() = runTest {

        coEvery {
            remoteMediator.initialize()
        } returns RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH

        coEvery {
            remoteMediator.load(any(), any())
        } returns RemoteMediator.MediatorResult.Success(endOfPaginationReached = true)

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
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                created = "2017-11-04T18:48:46.250Z"
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
        val result = repository.getCharacters(favoritesOnly = true).asSnapshot()

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
        repository.getCharacters(query = "rick", favoritesOnly = true).asSnapshot()

        // Then
        verify { charactersDao.searchFavorites("rick") }
        verify(exactly = 0) { charactersDao.getFavorites() }
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
