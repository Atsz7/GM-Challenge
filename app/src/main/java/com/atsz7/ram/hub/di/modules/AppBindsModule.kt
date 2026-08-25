package com.atsz7.ram.hub.di.modules

import com.atsz7.ram.hub.data.repositories.CharactersRepositoryImpl
import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        implementation: CharactersRepositoryImpl
    ): CharactersRepository
}
