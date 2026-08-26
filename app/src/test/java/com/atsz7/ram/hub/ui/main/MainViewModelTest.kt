package com.atsz7.ram.hub.ui.main

import androidx.paging.PagingData
import com.atsz7.ram.hub.domain.usecases.GetCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveFavoriteIdsUseCase
import com.atsz7.ram.hub.domain.usecases.RefreshCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.ToggleFavoriteUseCase
import com.atsz7.ram.hub.ui.main.models.CharactersFilter
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val getCharactersUseCase: GetCharactersUseCase = mockk()
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase = mockk()
    private val refreshCharactersUseCase: RefreshCharactersUseCase = mockk(relaxed = true)
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getCharactersUseCase(any(), any()) } returns flowOf(PagingData.empty())
        every { observeFavoriteIdsUseCase() } returns flowOf(emptySet())
    }

    private fun buildViewModel() = MainViewModel(
        getCharactersUseCase,
        observeFavoriteIdsUseCase,
        refreshCharactersUseCase,
        toggleFavoriteUseCase
    )

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Suppress("UnusedFlow")
    @Test
    fun `characters flow queries with an empty search by default`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.characters.first()

        // Then
        verify { getCharactersUseCase("", false) }
    }

    @Suppress("UnusedFlow")
    @Test
    fun `onSearchQueryChange updates searchQuery and re-queries characters`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.onSearchQueryChange("Rick")
        viewModel.characters.first()

        // Then
        assertEquals("Rick", viewModel.searchQuery.value)
        verify { getCharactersUseCase("Rick", false) }
    }

    @Suppress("UnusedFlow")
    @Test
    fun `onFilterChange updates filter and re-queries characters as favorites-only`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.onFilterChange(CharactersFilter.FAVORITES)
        viewModel.characters.first()

        // Then
        assertEquals(CharactersFilter.FAVORITES, viewModel.filter.value)
        verify { getCharactersUseCase("", true) }
    }

    @Test
    fun `onPullToRefresh calls refreshCharactersUseCase`() {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.onPullToRefresh()

        // Then
        verify { refreshCharactersUseCase() }
    }

    @Test
    fun `onToggleFavorite marks a non-favorite character as favorite`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.onToggleFavorite(id = 1)

        // Then
        coVerify { toggleFavoriteUseCase(id = 1, isFavorite = true) }
    }

    @Test
    fun `onToggleFavorite unmarks an already favorite character`() = runTest {

        // Given
        every { observeFavoriteIdsUseCase() } returns flowOf(setOf(1))
        val viewModel = buildViewModel()
        viewModel.favoriteIds.first { it.contains(1) }

        // When
        viewModel.onToggleFavorite(id = 1)

        // Then
        coVerify { toggleFavoriteUseCase(id = 1, isFavorite = false) }
    }
}
