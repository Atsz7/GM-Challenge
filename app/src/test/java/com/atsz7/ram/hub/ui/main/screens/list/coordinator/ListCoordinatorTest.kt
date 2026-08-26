package com.atsz7.ram.hub.ui.main.screens.list.coordinator

import androidx.paging.PagingData
import com.atsz7.ram.hub.ui.main.screens.list.ListViewModel
import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter
import com.atsz7.ram.hub.ui.main.screens.list.state.ListScreenState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

class ListCoordinatorTest {

    private val viewModel: ListViewModel = mockk(relaxed = true)
    private val onNavigateToDetail: (Int) -> Unit = mockk(relaxed = true)

    private fun buildCoordinator(): ListCoordinator {
        every { viewModel.uiState } returns MutableStateFlow(ListScreenState())
        every { viewModel.characters } returns flowOf(PagingData.empty())
        return ListCoordinator(viewModel, onNavigateToDetail)
    }

    @Test
    fun `onSearchQueryChange delegates to the ViewModel`() {

        // Given
        val coordinator = buildCoordinator()

        // When
        coordinator.onSearchQueryChange("Rick")

        // Then
        verify { viewModel.onSearchQueryChange("Rick") }
    }

    @Test
    fun `onFilterChange delegates to the ViewModel`() {

        // Given
        val coordinator = buildCoordinator()

        // When
        coordinator.onFilterChange(CharactersFilter.FAVORITES)

        // Then
        verify { viewModel.onFilterChange(CharactersFilter.FAVORITES) }
    }

    @Test
    fun `onPullToRefresh delegates to the ViewModel`() {

        // Given
        val coordinator = buildCoordinator()

        // When
        coordinator.onPullToRefresh()

        // Then
        verify { viewModel.onPullToRefresh() }
    }

    @Test
    fun `onToggleFavorite delegates to the ViewModel`() {

        // Given
        val coordinator = buildCoordinator()

        // When
        coordinator.onToggleFavorite(1)

        // Then
        verify { viewModel.onToggleFavorite(1) }
    }

    @Test
    fun `onCharacterClick triggers navigation instead of calling the ViewModel`() {

        // Given
        val coordinator = buildCoordinator()

        // When
        coordinator.onCharacterClick(1)

        // Then
        verify { onNavigateToDetail(1) }
    }
}
