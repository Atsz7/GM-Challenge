package com.atsz7.ram.hub.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.local.entities.RemoteKeysEntity
import com.atsz7.ram.hub.core.data.remote.api.RamHubApi
import com.atsz7.ram.hub.core.data.remote.dto.CharacterPlace
import com.atsz7.ram.hub.core.data.remote.dto.CharacterResponse
import com.atsz7.ram.hub.core.data.remote.dto.CharacterResult
import com.atsz7.ram.hub.core.data.remote.dto.PageInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class CharacterRemoteMediatorTest {

    private lateinit var database: RamHubDatabase
    private val api: RamHubApi = mockk()
    private lateinit var mediator: CharacterRemoteMediator

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RamHubDatabase::class.java
        ).build()
        mediator = CharacterRemoteMediator(api, database)
    }

    private fun character(id: Int) = CharacterEntity(
        id = id,
        name = "Character $id",
        nameNormalized = "character $id",
        status = "Alive",
        specie = "Human",
        type = "",
        gender = "Male",
        originName = "Earth",
        locationName = "Earth",
        image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
        created = "2017-11-04T18:48:46.250Z"
    )

    private fun characterResult(id: Int) = CharacterResult(
        id = id,
        name = "Character $id",
        status = "Alive",
        specie = "Human",
        type = "",
        gender = "Male",
        origin = CharacterPlace(name = "Earth", url = ""),
        location = CharacterPlace(name = "Earth", url = ""),
        image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
        url = "",
        created = "2017-11-04T18:48:46.250Z"
    )

    private fun response(next: String?, vararg results: CharacterResult) = CharacterResponse(
        info = PageInfo(count = results.size, pages = 1, next = next),
        results = results.toList()
    )

    private fun emptyPagingState() = PagingState<Int, CharacterEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0
    )

    private fun successOf(result: RemoteMediator.MediatorResult) =
        (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached

    @Test
    fun initialize_returnsLaunchInitialRefresh_whenTheDatabaseIsEmpty() = runBlocking {

        // When
        val result = mediator.initialize()

        // Then
        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
    }

    @Test
    fun initialize_returnsSkipInitialRefresh_whenTheDatabaseAlreadyHasCharacters() = runBlocking {

        // Given
        database.charactersDao().upsertAll(listOf(character(1)))

        // When
        val result = mediator.initialize()

        // Then
        assertEquals(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH, result)
    }

    @Test
    fun load_withPrepend_reachesEndOfPagination_withoutCallingTheApi() = runBlocking {

        // When
        val result = mediator.load(LoadType.PREPEND, emptyPagingState())

        // Then
        assertTrue(successOf(result))
        coVerify(exactly = 0) { api.getCharacters(any()) }
    }

    @Test
    fun load_withRefresh_requestsPageOne_andStoresTheResults() = runBlocking {

        // Given
        coEvery { api.getCharacters(page = 1) } returns response(next = null, characterResult(1), characterResult(2))

        // When
        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        // Then
        assertTrue(successOf(result))
        assertEquals(2, database.charactersDao().count())
        assertNull(database.remoteKeysDao().get()?.nextPage)
    }

    @Test
    fun load_withRefresh_storesTheNextPage_whenTheApiReportsMorePages() = runBlocking {

        // Given
        coEvery {
            api.getCharacters(page = 1)
        } returns response(next = "https://rickandmortyapi.com/api/character?page=2", characterResult(1))

        // When
        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        // Then
        assertFalse(successOf(result))
        assertEquals(2, database.remoteKeysDao().get()?.nextPage)
    }

    @Test
    fun load_withRefresh_andForceRefresh_clearsExistingCharactersFirst() = runBlocking {

        // Given
        database.charactersDao().upsertAll(listOf(character(99)))
        mediator.forceRefresh = true
        coEvery { api.getCharacters(page = 1) } returns response(next = null, characterResult(1))

        // When
        mediator.load(LoadType.REFRESH, emptyPagingState())

        // Then
        assertNull(database.charactersDao().getCharacterById(99).first())
        assertEquals(1, database.charactersDao().count())
        assertFalse(mediator.forceRefresh)
    }

    @Test
    fun load_withRefresh_andNoForceRefresh_keepsExistingCharacters() = runBlocking {

        // Given
        database.charactersDao().upsertAll(listOf(character(99)))
        coEvery { api.getCharacters(page = 1) } returns response(next = null, characterResult(1))

        // When
        mediator.load(LoadType.REFRESH, emptyPagingState())

        // Then
        assertEquals(2, database.charactersDao().count())
    }

    @Test
    fun load_withAppend_reachesEndOfPagination_whenThereIsNoStoredNextPage() = runBlocking {

        // When
        val result = mediator.load(LoadType.APPEND, emptyPagingState())

        // Then
        assertTrue(successOf(result))
        coVerify(exactly = 0) { api.getCharacters(any()) }
    }

    @Test
    fun load_withAppend_requestsTheStoredNextPage() = runBlocking {

        // Given
        database.remoteKeysDao().upsert(RemoteKeysEntity(nextPage = 3))
        coEvery { api.getCharacters(page = 3) } returns response(next = null, characterResult(1))

        // When
        val result = mediator.load(LoadType.APPEND, emptyPagingState())

        // Then
        assertTrue(successOf(result))
        coVerify { api.getCharacters(page = 3) }
    }

    @Test
    fun load_returnsError_whenTheApiThrowsAnIOException() = runBlocking {

        // Given: a checked IOException can't be thrown through MockK's Android interface
        // proxy (java.lang.reflect.Proxy wraps it in UndeclaredThrowableException), so a
        // real fake implementation is used here instead of coEvery { ... } throws.
        val exception = IOException("no network")
        val throwingApi = object : RamHubApi {
            override suspend fun getCharacters(page: Int): CharacterResponse = throw exception
        }
        val mediator = CharacterRemoteMediator(throwingApi, database)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        // Then
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
    }

    @Test
    fun load_returnsError_whenTheApiThrowsAnHttpException() = runBlocking {

        // Given
        val exception = HttpException(Response.error<Any>(404, "".toResponseBody(null)))
        coEvery { api.getCharacters(page = 1) } throws exception

        // When
        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        // Then
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
    }

    @After
    fun tearDown() {
        database.close()
    }
}
