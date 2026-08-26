package com.atsz7.ram.hub.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeRepositoryImplTest {

    private val dataStore: DataStore<Preferences> = mockk()
    private val systemThemeProvider: SystemThemeProvider = mockk()
    private val repository = ThemeRepositoryImpl(dataStore, systemThemeProvider)

    @Test
    fun `observeIsDarkMode emits the stored preference when present`() = runTest {

        // Given
        every { dataStore.data } returns flowOf(preferencesOf(IS_DARK_MODE_KEY to true))

        // When
        val result = repository.observeIsDarkMode().first()

        // Then
        assertTrue(result)
    }

    @Test
    fun `observeIsDarkMode falls back to the system default when no preference is stored`() = runTest {

        // Given
        every { dataStore.data } returns flowOf(emptyPreferences())
        every { systemThemeProvider.isSystemInDarkMode() } returns true

        // When
        val result = repository.observeIsDarkMode().first()

        // Then
        assertTrue(result)
    }

    @Test
    fun `setDarkMode persists the value in the DataStore`() = runTest {

        // Given
        var storedPreferences: Preferences = emptyPreferences()
        coEvery { dataStore.updateData(any()) } coAnswers {
            storedPreferences = firstArg<suspend (Preferences) -> Preferences>().invoke(storedPreferences)
            storedPreferences
        }

        // When
        repository.setDarkMode(true)

        // Then
        assertEquals(true, storedPreferences[IS_DARK_MODE_KEY])
    }

    private companion object {
        val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }
}
