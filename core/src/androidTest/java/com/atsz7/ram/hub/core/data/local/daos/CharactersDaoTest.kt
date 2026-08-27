package com.atsz7.ram.hub.core.data.local.daos

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.local.entities.FavoriteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharactersDaoTest {

    private lateinit var database: RamHubDatabase
    private lateinit var charactersDao: CharactersDao
    private lateinit var favoritesDao: FavoritesDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RamHubDatabase::class.java
        ).build()
        charactersDao = database.charactersDao()
        favoritesDao = database.favoritesDao()
    }

    private fun character(id: Int, name: String, nameNormalized: String = name.lowercase()) =
        CharacterEntity(
            id = id,
            name = name,
            nameNormalized = nameNormalized,
            status = "Alive",
            specie = "Human",
            type = "",
            gender = "Male",
            originName = "Earth",
            locationName = "Earth",
            image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
            created = "2017-11-04T18:48:46.250Z"
        )

    private suspend fun PagingSource<Int, CharacterEntity>.loadAll(): List<CharacterEntity> {
        val result = load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        return result.data
    }

    @Test
    fun upsertAll_insertsCharacters_andGetAllReturnsThemOrderedById() = runBlocking {

        // Given
        charactersDao.upsertAll(
            listOf(
                character(id = 2, name = "Morty Smith", nameNormalized = "morty smith"),
                character(id = 1, name = "Rick Sanchez", nameNormalized = "rick sanchez")
            )
        )

        // When
        val result = charactersDao.getAll().loadAll()

        // Then
        assertEquals(listOf(1, 2), result.map { it.id })
    }

    @Test
    fun upsertAll_updatesExistingCharacter_insteadOfDuplicatingIt() = runBlocking {

        // Given
        charactersDao.upsertAll(listOf(character(id = 1, name = "Rick Sanchez")))
        charactersDao.upsertAll(listOf(character(id = 1, name = "Rick Prime")))

        // When
        val result = charactersDao.getAll().loadAll()

        // Then
        assertEquals(1, result.size)
        assertEquals("Rick Prime", result.single().name)
    }

    @Test
    fun search_ranksPrefixMatches_beforeMatchesInTheMiddleOfTheName() = runBlocking {

        // Given: "Rick Sanchez" contains "san" mid-name, "Sandra" starts with it.
        // Rick Sanchez is inserted first (lower id) so a plain "id ASC" order would rank it first.
        charactersDao.upsertAll(
            listOf(
                character(id = 1, name = "Rick Sanchez", nameNormalized = "rick sanchez"),
                character(id = 2, name = "Sandra", nameNormalized = "sandra")
            )
        )

        // When
        val result = charactersDao.search("san").loadAll()

        // Then
        assertEquals(listOf("Sandra", "Rick Sanchez"), result.map { it.name })
    }

    @Test
    fun search_withNoMatches_returnsAnEmptyPage() = runBlocking {

        // Given
        charactersDao.upsertAll(listOf(character(id = 1, name = "Rick Sanchez", nameNormalized = "rick sanchez")))

        // When
        val result = charactersDao.search("zzz").loadAll()

        // Then
        assertEquals(emptyList<CharacterEntity>(), result)
    }

    @Test
    fun getFavorites_returnsOnlyCharactersMarkedAsFavorite() = runBlocking {

        // Given
        charactersDao.upsertAll(
            listOf(
                character(id = 1, name = "Rick Sanchez"),
                character(id = 2, name = "Morty Smith")
            )
        )
        favoritesDao.add(FavoriteEntity(characterId = 2))

        // When
        val result = charactersDao.getFavorites().loadAll()

        // Then
        assertEquals(listOf("Morty Smith"), result.map { it.name })
    }

    @Test
    fun getFavorites_excludesCharactersAfterTheyAreRemovedFromFavorites() = runBlocking {

        // Given
        charactersDao.upsertAll(listOf(character(id = 1, name = "Rick Sanchez")))
        favoritesDao.add(FavoriteEntity(characterId = 1))
        favoritesDao.remove(characterId = 1)

        // When
        val result = charactersDao.getFavorites().loadAll()

        // Then
        assertEquals(emptyList<CharacterEntity>(), result)
    }

    @Test
    fun searchFavorites_combinesTheFavoritesJoin_withThePrefixRanking() = runBlocking {

        // Given
        charactersDao.upsertAll(
            listOf(
                character(id = 1, name = "Rick Sanchez", nameNormalized = "rick sanchez"),
                character(id = 2, name = "Sandra", nameNormalized = "sandra"),
                character(id = 3, name = "Morty Smith", nameNormalized = "morty smith")
            )
        )
        favoritesDao.add(FavoriteEntity(characterId = 1))
        favoritesDao.add(FavoriteEntity(characterId = 2))
        // Morty Smith stays a non-favorite so it can never leak into the joined result.

        // When
        val result = charactersDao.searchFavorites("san").loadAll()

        // Then
        assertEquals(listOf("Sandra", "Rick Sanchez"), result.map { it.name })
    }

    @Test
    fun count_returnsTheNumberOfStoredCharacters() = runBlocking {

        // Given
        charactersDao.upsertAll(
            listOf(character(id = 1, name = "Rick Sanchez"), character(id = 2, name = "Morty Smith"))
        )

        // When
        val result = charactersDao.count()

        // Then
        assertEquals(2, result)
    }

    @Test
    fun clearAll_removesEveryStoredCharacter() = runBlocking {

        // Given
        charactersDao.upsertAll(listOf(character(id = 1, name = "Rick Sanchez")))

        // When
        charactersDao.clearAll()

        // Then
        assertEquals(0, charactersDao.count())
    }

    @After
    fun tearDown() {
        database.close()
    }
}
