package com.atsz7.ram.hub.domain.usecases

import com.atsz7.ram.hub.domain.repositories.ThemeRepository
import javax.inject.Inject

class SetDarkModeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    suspend operator fun invoke(isDark: Boolean) {
        repository.setDarkMode(isDark)
    }
}
