package com.atsz7.ram.hub.di.modules

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.atsz7.ram.hub.ui.main.navigation.MainRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DetailRouteModule {

    @Provides
    fun provideDetailRoute(savedStateHandle: SavedStateHandle): MainRoute.Detail =
        savedStateHandle.toRoute()
}
