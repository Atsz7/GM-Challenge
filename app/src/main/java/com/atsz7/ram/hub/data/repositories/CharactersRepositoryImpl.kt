package com.atsz7.ram.hub.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.mappers.toDomain
import com.atsz7.ram.hub.core.data.paging.CharacterRemoteMediator
import com.atsz7.ram.hub.core.data.remote.api.RamHubApi
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(
    api: RamHubApi,
    private val database: RamHubDatabase
) : CharactersRepository {

    private val remoteMediator = CharacterRemoteMediator(api, database)

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
                initialLoadSize = ITEMS_PER_PAGE * 2,
                prefetchDistance = PRE_FETCH_DISTANCE
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { database.charactersDao().getAll() }
        ).flow.map { pagingData ->
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
