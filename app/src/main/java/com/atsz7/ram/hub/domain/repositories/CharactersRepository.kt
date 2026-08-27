package com.atsz7.ram.hub.domain.repositories

import androidx.paging.PagingData
import com.atsz7.ram.hub.domain.model.Character
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {

    fun getCharacters(
        query: String = "",
        favoritesOnly: Boolean = false
    ): Flow<PagingData<Character>>

    fun requestForceRefresh()
    fun getCharacterById(id: Int): Flow<Character?>
    fun observeFavoriteIds(): Flow<ImmutableSet<Int>>
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)
}
