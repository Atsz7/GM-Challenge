package com.atsz7.ram.hub.ui.main.screens.detail

import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.usecases.GetCharacterByIdUseCase
import com.atsz7.ram.hub.domain.usecases.ObserveFavoriteIdsUseCase
import com.atsz7.ram.hub.domain.usecases.ToggleFavoriteUseCase
import com.atsz7.ram.hub.ui.main.navigation.MainRoute
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
class DetailViewModelTest {

    private val getCharacterByIdUseCase: GetCharacterByIdUseCase = mockk()
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val character = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        specie = "Human",
        gender = "Male",
        originName = "Earth",
        locationName = "Earth",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        createdAt = "2017-11-05T11:53:44.737Z"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getCharacterByIdUseCase(1) } returns flowOf(character)
        every { observeFavoriteIdsUseCase() } returns flowOf(emptySet())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = DetailViewModel(
        MainRoute.Detail(characterId = 1),
        getCharacterByIdUseCase,
        observeFavoriteIdsUseCase,
        toggleFavoriteUseCase
    )

    @Test
    fun `uiState exposes the character fetched by id`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        val result = viewModel.uiState.first { it.character != null }

        // Then
        assertEquals("Rick Sanchez", result.character?.name)
    }

    @Test
    fun `uiState reflects isFavorite from the favorite ids`() = runTest {

        // Given
        every { observeFavoriteIdsUseCase() } returns flowOf(setOf(1))
        val viewModel = buildViewModel()

        // When
        val result = viewModel.uiState.first { it.character != null }

        // Then
        assertEquals(true, result.isFavorite)
    }

    @Test
    fun `onToggleFavorite marks a non-favorite character as favorite`() = runTest {

        // Given
        val viewModel = buildViewModel()

        // When
        viewModel.onToggleFavorite()

        // Then
        coVerify { toggleFavoriteUseCase(id = 1, isFavorite = true) }
    }

    @Test
    fun `onToggleFavorite unmarks an already favorite character`() = runTest {

        // Given
        every { observeFavoriteIdsUseCase() } returns flowOf(setOf(1))
        val viewModel = buildViewModel()
        viewModel.uiState.first { it.isFavorite }

        // When
        viewModel.onToggleFavorite()

        // Then
        coVerify { toggleFavoriteUseCase(id = 1, isFavorite = false) }
    }
}
