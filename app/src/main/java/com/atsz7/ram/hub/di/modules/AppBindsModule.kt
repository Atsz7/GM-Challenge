package com.atsz7.ram.hub.di.modules

import com.atsz7.ram.hub.data.repositories.CharactersRepositoryImpl
import com.atsz7.ram.hub.data.repositories.SystemThemeProvider
import com.atsz7.ram.hub.data.repositories.SystemThemeProviderImpl
import com.atsz7.ram.hub.data.repositories.ThemeRepositoryImpl
import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import com.atsz7.ram.hub.domain.repositories.ThemeRepository
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

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        implementation: ThemeRepositoryImpl
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindSystemThemeProvider(
        implementation: SystemThemeProviderImpl
    ): SystemThemeProvider
}
