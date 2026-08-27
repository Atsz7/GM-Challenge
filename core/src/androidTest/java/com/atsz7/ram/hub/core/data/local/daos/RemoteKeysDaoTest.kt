package com.atsz7.ram.hub.core.data.local.daos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.local.entities.RemoteKeysEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeysDaoTest {

    private lateinit var database: RamHubDatabase
    private lateinit var remoteKeysDao: RemoteKeysDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RamHubDatabase::class.java
        ).build()
        remoteKeysDao = database.remoteKeysDao()
    }

    @Test
    fun get_returnsNull_whenNoRemoteKeysHaveBeenStored() = runBlocking {

        // When
        val result = remoteKeysDao.get()

        // Then
        assertNull(result)
    }

    @Test
    fun upsert_storesTheRemoteKeys_andGetReturnsThem() = runBlocking {

        // Given
        remoteKeysDao.upsert(RemoteKeysEntity(nextPage = 2))

        // When
        val result = remoteKeysDao.get()

        // Then
        assertEquals(2, result?.nextPage)
    }

    @Test
    fun upsert_overwritesTheSingletonRow_insteadOfInsertingASecondOne() = runBlocking {

        // Given
        remoteKeysDao.upsert(RemoteKeysEntity(nextPage = 2))
        remoteKeysDao.upsert(RemoteKeysEntity(nextPage = 5))

        // When
        val result = remoteKeysDao.get()

        // Then
        assertEquals(5, result?.nextPage)
    }

    @Test
    fun upsert_withANullNextPage_representsTheEndOfPagination() = runBlocking {

        // Given
        remoteKeysDao.upsert(RemoteKeysEntity(nextPage = null))

        // When
        val result = remoteKeysDao.get()

        // Then
        assertNull(result?.nextPage)
    }

    @After
    fun tearDown() {
        database.close()
    }
}
