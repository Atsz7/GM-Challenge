package com.atsz7.ram.hub.core.data.local.daos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.local.entities.FavoriteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    private lateinit var database: RamHubDatabase
    private lateinit var favoritesDao: FavoritesDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RamHubDatabase::class.java
        ).build()
        favoritesDao = database.favoritesDao()
    }

    @Test
    fun observeFavoriteIds_isEmpty_whenNoFavoritesHaveBeenAdded() = runBlocking {

        // When
        val result = favoritesDao.observeFavoriteIds().first()

        // Then
        assertEquals(emptyList<Int>(), result)
    }

    @Test
    fun observeFavoriteIds_reflectsAFavorite_afterItIsAdded() = runBlocking {

        // Given
        favoritesDao.add(FavoriteEntity(characterId = 1))

        // When
        val result = favoritesDao.observeFavoriteIds().first()

        // Then
        assertEquals(listOf(1), result)
    }

    @Test
    fun observeFavoriteIds_noLongerContainsACharacter_afterItIsRemoved() = runBlocking {

        // Given
        favoritesDao.add(FavoriteEntity(characterId = 1))
        favoritesDao.add(FavoriteEntity(characterId = 2))
        favoritesDao.remove(characterId = 1)

        // When
        val result = favoritesDao.observeFavoriteIds().first()

        // Then
        assertEquals(listOf(2), result)
    }

    @Test
    fun add_isIdempotent_forTheSameCharacterId() = runBlocking {

        // Given
        favoritesDao.add(FavoriteEntity(characterId = 1))
        favoritesDao.add(FavoriteEntity(characterId = 1))

        // When
        val result = favoritesDao.observeFavoriteIds().first()

        // Then
        assertEquals(listOf(1), result)
    }

    @After
    fun tearDown() {
        database.close()
    }
}
