package com.atsz7.ram.hub.ui.main.screens.detail.coordinator

import com.atsz7.ram.hub.ui.main.screens.detail.viewmodels.DetailViewModel
import com.atsz7.ram.hub.ui.main.screens.detail.state.DetailScreenState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class DetailCoordinatorTest {

    private val viewModel: DetailViewModel = mockk(relaxed = true)
    private val onBackClick: () -> Unit = mockk(relaxed = true)

    private fun buildCoordinator(): DetailCoordinator {
        every { viewModel.uiState } returns MutableStateFlow(DetailScreenState())
        return DetailCoordinator(viewModel, onBackClick)
    }

    @Test
    fun `onToggleFavorite delegates to the ViewModel`() {

        // Given
        val coordinator = buildCoordinator()

        // When
        coordinator.onToggleFavorite()

        // Then
        verify { viewModel.onToggleFavorite() }
    }

    @Test
    fun `onBackClick triggers navigation instead of calling the ViewModel`() {

        // Given
        val coordinator = buildCoordinator()

        // When
        coordinator.onBackClick()

        // Then
        verify { onBackClick() }
    }
}
