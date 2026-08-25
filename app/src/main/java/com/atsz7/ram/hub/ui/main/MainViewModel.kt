package com.atsz7.ram.hub.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.usecases.GetCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.RefreshCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    getCharactersUseCase: GetCharactersUseCase,
    private val refreshCharactersUseCase: RefreshCharactersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val characters: Flow<PagingData<Character>> = _searchQuery
        .debounce(SEARCH_DEBOUNCE_MILLIS.milliseconds)
        .flatMapLatest { query -> getCharactersUseCase(query) }
        .cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onPullToRefresh() {
        refreshCharactersUseCase()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
