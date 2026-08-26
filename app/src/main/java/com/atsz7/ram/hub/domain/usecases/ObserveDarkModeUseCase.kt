package com.atsz7.ram.hub.domain.usecases

import com.atsz7.ram.hub.domain.repositories.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDarkModeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeIsDarkMode()
}
