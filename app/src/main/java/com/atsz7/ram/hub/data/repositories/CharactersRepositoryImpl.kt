package com.atsz7.ram.hub.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.atsz7.ram.hub.core.extensions.toNormalizedSearchTerm
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.mappers.toDomain
import com.atsz7.ram.hub.core.data.paging.CharacterRemoteMediator
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(
    private val database: RamHubDatabase,
    private val remoteMediator: CharacterRemoteMediator
) : CharactersRepository {

    private val pagingConfig = PagingConfig(
        pageSize = ITEMS_PER_PAGE,
        enablePlaceholders = false,
        initialLoadSize = ITEMS_PER_PAGE * 2,
        prefetchDistance = PRE_FETCH_DISTANCE
    )

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(query: String): Flow<PagingData<Character>> {

        val term = query.toNormalizedSearchTerm()

        // An active search is served entirely from the local database, so the
        // RemoteMediator is left out and remote pagination stays paused until cleared.
        val pager = if (term.isBlank()) {
            Pager(
                config = pagingConfig,
                remoteMediator = remoteMediator,
                pagingSourceFactory = { database.charactersDao().getAll() }
            )
        } else {
            Pager(
                config = pagingConfig,
                pagingSourceFactory = { database.charactersDao().search(term) }
            )
        }

        return pager.flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override fun requestForceRefresh() {
        remoteMediator.forceRefresh = true
    }

    companion object {
        private const val ITEMS_PER_PAGE = 20
        private const val PRE_FETCH_DISTANCE = 1
    }
}
