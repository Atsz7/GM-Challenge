package com.atsz7.ram.hub.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.atsz7.ram.hub.domain.repositories.ThemeRepository
import com.atsz7.ram.hub.domain.system.SystemThemeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val systemThemeProvider: SystemThemeProvider
) : ThemeRepository {

    override fun observeIsDarkMode(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: systemThemeProvider.isSystemInDarkMode()
        }
    }

    override suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences -> preferences[IS_DARK_MODE_KEY] = isDark }
    }

    companion object {
        private val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }
}
