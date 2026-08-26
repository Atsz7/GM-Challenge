package com.atsz7.ram.hub.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.atsz7.ram.hub.core.extensions.toNormalizedSearchTerm
import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.daos.FavoritesDao
import com.atsz7.ram.hub.core.data.local.entities.FavoriteEntity
import com.atsz7.ram.hub.core.data.mappers.toDomain
import com.atsz7.ram.hub.core.data.paging.CharacterRemoteMediator
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
    private val favoritesDao: FavoritesDao,
    private val remoteMediator: CharacterRemoteMediator
) : CharactersRepository {

    private val pagingConfig = PagingConfig(
        pageSize = ITEMS_PER_PAGE,
        enablePlaceholders = false,
        initialLoadSize = ITEMS_PER_PAGE * 2,
        prefetchDistance = PRE_FETCH_DISTANCE
    )

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(
        query: String,
        favoritesOnly: Boolean
    ): Flow<PagingData<Character>> {

        val term = query.toNormalizedSearchTerm()

        // Favorites are a filter over what's already stored locally, so - just like an
        // active search - the RemoteMediator is left out and remote pagination is skipped.
        val pager = when {

            favoritesOnly && term.isBlank() -> Pager(
                config = pagingConfig,
                pagingSourceFactory = { charactersDao.getFavorites() }
            )

            favoritesOnly -> Pager(
                config = pagingConfig,
                pagingSourceFactory = { charactersDao.searchFavorites(term) }
            )

            term.isBlank() -> Pager(
                config = pagingConfig,
                remoteMediator = remoteMediator,
                pagingSourceFactory = { charactersDao.getAll() }
            )

            else -> Pager(
                config = pagingConfig,
                pagingSourceFactory = { charactersDao.search(term) }
            )
        }

        return pager.flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override fun requestForceRefresh() {
        remoteMediator.forceRefresh = true
    }

    override fun getCharacterById(id: Int): Flow<Character?> {
        return charactersDao.getCharacterById(id).map { entity -> entity?.toDomain() }
    }

    override fun observeFavoriteIds(): Flow<Set<Int>> {
        return favoritesDao.observeFavoriteIds().map { it.toSet() }
    }

    override suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favoritesDao.add(FavoriteEntity(characterId = id))
        } else {
            favoritesDao.remove(id)
        }
    }

    companion object {
        private const val ITEMS_PER_PAGE = 20
        private const val PRE_FETCH_DISTANCE = 1
    }
}
