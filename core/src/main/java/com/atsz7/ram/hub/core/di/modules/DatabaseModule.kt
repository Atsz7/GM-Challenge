package com.atsz7.ram.hub.core.di.modules

import android.content.Context
import androidx.room.Room
import com.atsz7.ram.hub.core.data.local.daos.CharactersDao
import com.atsz7.ram.hub.core.data.local.database.RamHubDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RamHubDatabase =
        Room.databaseBuilder(context, RamHubDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideCharactersDao(database: RamHubDatabase): CharactersDao {
        return database.charactersDao()
    }

    private const val DATABASE_NAME = "ram_hub.db"
}
