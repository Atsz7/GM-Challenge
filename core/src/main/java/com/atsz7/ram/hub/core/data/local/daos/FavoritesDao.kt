package com.atsz7.ram.hub.core.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.atsz7.ram.hub.core.data.local.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT characterId FROM favorites")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Upsert
    suspend fun add(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE characterId = :characterId")
    suspend fun remove(characterId: Int)
}
