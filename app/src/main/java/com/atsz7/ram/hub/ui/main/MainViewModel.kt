package com.atsz7.ram.hub.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.usecases.GetCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveFavoriteIdsUseCase
import com.atsz7.ram.hub.domain.usecases.RefreshCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.ToggleFavoriteUseCase
import com.atsz7.ram.hub.ui.main.models.CharactersFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    getCharactersUseCase: GetCharactersUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val refreshCharactersUseCase: RefreshCharactersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(CharactersFilter.ALL)
    val filter: StateFlow<CharactersFilter> = _filter.asStateFlow()

    /**
     * Set of characters marked as favorites to display in the list
     * and determine whether a character is a favorite or not.
     */
    val favoriteIds: StateFlow<Set<Int>> = observeFavoriteIdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = FAVORITE_IDS_STOP_TIMEOUT_MILLIS
            ),
            initialValue = emptySet()
        )

    val characters: Flow<PagingData<Character>> = combine(
        _searchQuery.debounce(SEARCH_DEBOUNCE_MILLIS.milliseconds),
        _filter
    ) { query, filter -> query to filter }
        .flatMapLatest { (query, filter) ->
            getCharactersUseCase(
                query = query,
                favoritesOnly = filter == CharactersFilter.FAVORITES
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
        viewModelScope.launch {
            val isCurrentlyFavorite = favoriteIds.value.contains(id)
            toggleFavoriteUseCase(id = id, isFavorite = !isCurrentlyFavorite)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
        private const val FAVORITE_IDS_STOP_TIMEOUT_MILLIS = 5_000L
    }
}
