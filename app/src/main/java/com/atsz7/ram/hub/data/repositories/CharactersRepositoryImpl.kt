package com.atsz7.ram.hub.data.repositories

import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.daos.FavoritesDao
import com.atsz7.ram.hub.core.data.local.entities.FavoriteEntity
import com.atsz7.ram.hub.data.mappers.toDomain
import com.atsz7.ram.hub.data.paging.CharacterRemoteMediator
import com.atsz7.ram.hub.domain.model.Character
import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
    private val favoritesDao: FavoritesDao,
    private val remoteMediator: CharacterRemoteMediator
) : CharactersRepository {

    override fun requestForceRefresh() {
        remoteMediator.forceRefresh = true
    }

    override fun getCharacterById(id: Int): Flow<Character?> {
        return charactersDao.getCharacterById(id).map { entity -> entity?.toDomain() }
    }

    override fun observeFavoriteIds(): Flow<ImmutableSet<Int>> {
        return favoritesDao.observeFavoriteIds().map { it.toImmutableSet() }
    }

    override suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favoritesDao.add(FavoriteEntity(characterId = id))
        } else {
            favoritesDao.remove(id)
        }
    }
}
