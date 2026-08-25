package com.atsz7.ram.hub.core.data.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity

@Dao
interface CharactersDao {

    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAll(): PagingSource<Int, CharacterEntity>

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun count(): Int

    @Query("SELECT * FROM characters ORDER BY id DESC LIMIT 1")
    suspend fun getLastCharacter(): CharacterEntity?

    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}
