package com.atsz7.ram.hub.ui.main.screens.detail.coordinator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atsz7.ram.hub.ui.main.screens.detail.viewmodels.DetailViewModel
import com.atsz7.ram.hub.ui.main.screens.detail.state.DetailScreenState
import kotlinx.coroutines.flow.StateFlow

class DetailCoordinator(
    val viewModel: DetailViewModel,
    private val navigateBack: () -> Unit
) {
    val uiState: StateFlow<DetailScreenState> = viewModel.uiState

    fun onToggleFavorite() = viewModel.onToggleFavorite()
    fun onBackClick() = navigateBack()
}

@Composable
fun rememberDetailCoordinator(
    viewModel: DetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
): DetailCoordinator = remember(viewModel, onBackClick) {
    DetailCoordinator(viewModel, navigateBack = onBackClick)
}
