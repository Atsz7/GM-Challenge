package com.atsz7.ram.hub.ui.main

import androidx.paging.PagingData
import com.atsz7.ram.hub.domain.usecases.GetCharactersUseCase
import com.atsz7.ram.hub.domain.usecases.RefreshCharactersUseCase
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
    private val refreshCharactersUseCase: RefreshCharactersUseCase = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getCharactersUseCase(any()) } returns flowOf(PagingData.empty())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Suppress("UnusedFlow")
    @Test
    fun `characters flow queries with an empty search by default`() = runTest {

        // Given
        val viewModel = MainViewModel(getCharactersUseCase, refreshCharactersUseCase)

        // When
        viewModel.characters.first()

        // Then
        verify { getCharactersUseCase("") }
    }

    @Suppress("UnusedFlow")
    @Test
    fun `onSearchQueryChange updates searchQuery and re-queries characters`() = runTest {

        // Given
        val viewModel = MainViewModel(getCharactersUseCase, refreshCharactersUseCase)

        // When
        viewModel.onSearchQueryChange("Rick")
        viewModel.characters.first()

        // Then
        assertEquals("Rick", viewModel.searchQuery.value)
        verify { getCharactersUseCase("Rick") }
    }

    @Test
    fun `onPullToRefresh calls refreshCharactersUseCase`() {

        // Given
        val viewModel = MainViewModel(getCharactersUseCase, refreshCharactersUseCase)

        // When
        viewModel.onPullToRefresh()

        // Then
        verify { refreshCharactersUseCase() }
    }
}
