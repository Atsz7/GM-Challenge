package com.atsz7.ram.hub.ui.main.screens.list.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.usecases.GetCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveDarkModeUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveFavoriteIdsUseCase
import com.atsz7.ram.hub.domain.usecases.RefreshCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.SetDarkModeUseCase
import com.atsz7.ram.hub.domain.usecases.ToggleFavoriteUseCase
import com.atsz7.ram.hub.ui.main.viewmodels.base.MainViewModel
import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter
import com.atsz7.ram.hub.ui.main.screens.list.state.ListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ListViewModel @Inject constructor(
    getCharactersUseCase: GetCharactersUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val refreshCharactersUseCase: RefreshCharactersUseCase,
    toggleFavoriteUseCase: ToggleFavoriteUseCase,
    observeDarkModeUseCase: ObserveDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase
) : MainViewModel(observeFavoriteIdsUseCase, toggleFavoriteUseCase) {

    private val _searchQuery = MutableStateFlow("")
    private val _filter = MutableStateFlow(CharactersFilter.ALL)

    val uiState: StateFlow<ListScreenState> = combine(
        _searchQuery,
        _filter,
        favoriteIds,
        observeDarkModeUseCase()
    ) { query, filter, ids, isDarkMode ->
        ListScreenState(searchQuery = query, filter = filter, favoriteIds = ids, isDarkMode = isDarkMode)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            stopTimeoutMillis = STOP_TIMEOUT_MILLIS
        ),
        initialValue = ListScreenState()
    )

    val characters: Flow<PagingData<Character>> = combine(
        _searchQuery.debounce(SEARCH_DEBOUNCE_MILLIS.milliseconds),
        _filter
    ) { query, filter -> query to filter }
        .flatMapLatest { (query, filter) ->
            getCharactersUseCase(
                query = query,
                favoritesOnly = filter.isFavorites()
            )
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: CharactersFilter) {
        _filter.value = filter
    }

    fun onPullToRefresh() {
        refreshCharactersUseCase()
    }

    fun onToggleFavorite(id: Int) {
        toggleFavorite(id)
    }

    fun onToggleDarkMode() {
        viewModelScope.launch {
            setDarkModeUseCase(!uiState.value.isDarkMode)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
