package com.atsz7.ram.hub.core.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.atsz7.ram.hub.core.data.local.entities.RemoteKeysEntity

@Dao
interface RemoteKeysDao {

    @Upsert
    suspend fun upsert(remoteKeys: RemoteKeysEntity)

    @Query("SELECT * FROM characters_remote_keys WHERE id = 0")
    suspend fun get(): RemoteKeysEntity?
}
