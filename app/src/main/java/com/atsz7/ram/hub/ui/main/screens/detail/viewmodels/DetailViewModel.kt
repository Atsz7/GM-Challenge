package com.atsz7.ram.hub.ui.main.screens.detail.viewmodels

import androidx.lifecycle.viewModelScope
import com.atsz7.ram.hub.domain.usecases.GetCharacterByIdUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveFavoriteIdsUseCase
import com.atsz7.ram.hub.domain.usecases.ToggleFavoriteUseCase
import com.atsz7.ram.hub.ui.main.viewmodels.base.BaseFavoritesViewModel
import com.atsz7.ram.hub.ui.main.viewmodels.base.STOP_TIMEOUT_MILLIS
import com.atsz7.ram.hub.ui.main.navigation.MainRoute
import com.atsz7.ram.hub.ui.main.screens.detail.state.DetailScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    route: MainRoute.Detail,
    getCharacterByIdUseCase: GetCharacterByIdUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    toggleFavoriteUseCase: ToggleFavoriteUseCase
) : BaseFavoritesViewModel(observeFavoriteIdsUseCase, toggleFavoriteUseCase) {

    private val characterId: Int = route.characterId

    val uiState: StateFlow<DetailScreenState> = combine(
        getCharacterByIdUseCase(characterId),
        favoriteIds
    ) { character, ids ->
        DetailScreenState(
            character = character,
            isFavorite = character != null && character.id in ids
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        DetailScreenState()
    )

    fun onToggleFavorite() {
        toggleFavorite(characterId)
    }
}
