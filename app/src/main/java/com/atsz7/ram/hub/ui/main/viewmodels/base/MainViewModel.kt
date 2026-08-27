package com.atsz7.ram.hub.ui.main.viewmodels.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atsz7.ram.hub.domain.usecases.ObserveFavoriteIdsUseCase
import com.atsz7.ram.hub.domain.usecases.ToggleFavoriteUseCase
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class MainViewModel(
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    protected val favoriteIds: StateFlow<ImmutableSet<Int>> = observeFavoriteIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = persistentSetOf()
        )

    protected fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            val isCurrentlyFavorite = favoriteIds.value.contains(id)
            toggleFavoriteUseCase(id = id, isFavorite = !isCurrentlyFavorite)
        }
    }

    companion object {
        protected const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
