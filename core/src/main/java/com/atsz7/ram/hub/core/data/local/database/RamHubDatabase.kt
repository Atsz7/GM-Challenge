package com.atsz7.ram.hub.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.daos.RemoteKeysDao
import com.atsz7.ram.hub.core.data.local.entities.CharacterEntity
import com.atsz7.ram.hub.core.data.local.entities.RemoteKeysEntity

@Database(
    entities = [CharacterEntity::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RamHubDatabase : RoomDatabase() {
    abstract fun charactersDao(): CharactersDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
