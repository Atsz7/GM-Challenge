package com.atsz7.ram.hub.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.local.entities.RemoteKeysEntity
import com.atsz7.ram.hub.core.data.mappers.toEntity
import com.atsz7.ram.hub.core.data.remote.api.RamHubApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val ramHubApi: RamHubApi,
    private val database: RamHubDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    @Volatile
    var forceRefresh: Boolean = false

    override suspend fun initialize(): InitializeAction {
        return if (database.charactersDao().count() > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        return try {

            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val nextPage = database.remoteKeysDao().get()?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    nextPage
                }
            }

            val response = ramHubApi.getCharacters(page = page)
            val nextPage = if (response.info.next != null) page + 1 else null

            database.withTransaction {

                if (loadType == LoadType.REFRESH && forceRefresh) {
                    database.charactersDao().clearAll()
                    forceRefresh = false
                }

                val entities = response.results.map { it.toEntity() }
                database.charactersDao().upsertAll(entities)
                database.remoteKeysDao().upsert(RemoteKeysEntity(nextPage = nextPage))
            }

            MediatorResult.Success(endOfPaginationReached = nextPage == null)

        } catch (ex: IOException) {
            MediatorResult.Error(ex)
        } catch (ex: HttpException) {
            MediatorResult.Error(ex)
        }
    }
}
