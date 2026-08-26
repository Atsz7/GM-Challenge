package com.atsz7.ram.hub.domain.repositories

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {

    fun observeIsDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(isDark: Boolean)
}
