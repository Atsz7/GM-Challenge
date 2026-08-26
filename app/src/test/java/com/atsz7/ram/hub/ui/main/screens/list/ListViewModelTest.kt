package com.atsz7.ram.hub.ui.main.screens.list

import androidx.paging.PagingData
import com.atsz7.ram.hub.domain.usecases.GetCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveDarkModeUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveFavoriteIdsUseCase
import com.atsz7.ram.hub.domain.usecases.RefreshCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.SetDarkModeUseCase
import com.atsz7.ram.hub.domain.usecases.ToggleFavoriteUseCase
import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter
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
class ListViewModelTest {

    private val getCharactersUseCase: GetCharactersUseCase = mockk()
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase = mockk()
    private val refreshCharactersUseCase: RefreshCharactersUseCase = mockk(relaxed = true)
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)
    private val observeDarkModeUseCase: ObserveDarkModeUseCase = mockk()
    private val setDarkModeUseCase: SetDarkModeUseCase = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getCharactersUseCase(any(), any()) } returns flowOf(PagingData.empty())
        every { observeFavoriteIdsUseCase() } returns flowOf(emptySet())
        every { observeDarkModeUseCase() } returns flowOf(false)
    }

    private fun buildViewModel() = ListViewModel(
        getCharactersUseCase,
        observeFavoriteIdsUseCase,
        refreshCharactersUseCase,
        toggleFavoriteUseCase,
        observeDarkModeUseCase,
        setDarkModeUseCase
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
    fun `onSearchQueryChange updates uiState searchQuery and re-queries characters`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.onSearchQueryChange("Rick")
        viewModel.characters.first()

        // Then
        assertEquals("Rick", viewModel.uiState.first { it.searchQuery == "Rick" }.searchQuery)
        verify { getCharactersUseCase("Rick", false) }
    }

    @Suppress("UnusedFlow")
    @Test
    fun `onFilterChange updates uiState filter and re-queries characters as favorites-only`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.onFilterChange(CharactersFilter.FAVORITES)
        viewModel.characters.first()

        // Then
        assertEquals(CharactersFilter.FAVORITES, viewModel.uiState.first { it.filter == CharactersFilter.FAVORITES }.filter)
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
        viewModel.uiState.first { it.favoriteIds.contains(1) }

        // When
        viewModel.onToggleFavorite(id = 1)

        // Then
        coVerify { toggleFavoriteUseCase(id = 1, isFavorite = false) }
    }

    @Test
    fun `onToggleDarkMode switches from light to dark`() = runTest {

        // Given
        val viewModel = buildViewModel()
        viewModel.uiState.first()

        // When
        viewModel.onToggleDarkMode()

        // Then
        coVerify { setDarkModeUseCase(true) }
    }

    @Test
    fun `onToggleDarkMode switches from dark to light`() = runTest {

        // Given
        every { observeDarkModeUseCase() } returns flowOf(true)
        val viewModel = buildViewModel()
        viewModel.uiState.first { it.isDarkMode }

        // When
        viewModel.onToggleDarkMode()

        // Then
        coVerify { setDarkModeUseCase(false) }
    }
}
