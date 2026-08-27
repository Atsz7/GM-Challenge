package com.atsz7.ram.hub.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.extensions.toNormalizedSearchTerm
import com.atsz7.ram.hub.data.mappers.toDomain
import com.atsz7.ram.hub.domain.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharactersPagingProvider @Inject constructor(
    private val charactersDao: CharactersDao,
    private val remoteMediator: CharacterRemoteMediator
) {

    private val pagingConfig = PagingConfig(
        pageSize = ITEMS_PER_PAGE,
        enablePlaceholders = false,
        initialLoadSize = ITEMS_PER_PAGE * 2,
        prefetchDistance = PRE_FETCH_DISTANCE
    )

    @OptIn(ExperimentalPagingApi::class)
    fun getCharacters(
        query: String = "",
        favoritesOnly: Boolean = false
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

    companion object {
        private const val ITEMS_PER_PAGE = 20
        private const val PRE_FETCH_DISTANCE = 1
    }
}
