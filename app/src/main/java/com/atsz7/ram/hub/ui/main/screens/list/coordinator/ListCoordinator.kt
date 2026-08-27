package com.atsz7.ram.hub.ui.main.screens.list.coordinator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.PagingData
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.ui.main.screens.list.viewmodels.ListViewModel
import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter
import com.atsz7.ram.hub.ui.main.screens.list.state.ListScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ListCoordinator(
    val viewModel: ListViewModel,
    private val onNavigateToDetail: (Int) -> Unit
) {
    val uiState: StateFlow<ListScreenState> = viewModel.uiState
    val characters: Flow<PagingData<Character>> = viewModel.characters

    fun onSearchQueryChange(query: String) = viewModel.onSearchQueryChange(query)
    fun onFilterChange(filter: CharactersFilter) = viewModel.onFilterChange(filter)
    fun onPullToRefresh() = viewModel.onPullToRefresh()
    fun onToggleFavorite(id: Int) = viewModel.onToggleFavorite(id)
    fun onToggleDarkMode() = viewModel.onToggleDarkMode()
    fun onCharacterClick(id: Int) = onNavigateToDetail(id)
}

@Composable
fun rememberListCoordinator(
    viewModel: ListViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
): ListCoordinator = remember(viewModel, onNavigateToDetail) {
    ListCoordinator(viewModel, onNavigateToDetail)
}
