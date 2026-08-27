package com.atsz7.ram.hub.core.data.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharactersDao {

    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAll(): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Int): Flow<CharacterEntity?>

    @Query(
        """
        SELECT * FROM characters
        WHERE nameNormalized LIKE '%' || :term || '%'
        ORDER BY
            CASE WHEN nameNormalized LIKE :term || '%' THEN 0 ELSE 1 END,
            id ASC
        """
    )
    fun search(term: String): PagingSource<Int, CharacterEntity>

    @Query(
        """
        SELECT c.* FROM characters c
        INNER JOIN favorites f ON f.characterId = c.id
        ORDER BY c.id ASC
        """
    )
    fun getFavorites(): PagingSource<Int, CharacterEntity>

    @Query(
        """
        SELECT c.* FROM characters c
        INNER JOIN favorites f ON f.characterId = c.id
        WHERE c.nameNormalized LIKE '%' || :term || '%'
        ORDER BY
            CASE WHEN c.nameNormalized LIKE :term || '%' THEN 0 ELSE 1 END,
            c.id ASC
        """
    )
    fun searchFavorites(term: String): PagingSource<Int, CharacterEntity>

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}
