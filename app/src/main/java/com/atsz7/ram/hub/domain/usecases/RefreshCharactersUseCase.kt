package com.atsz7.ram.hub.domain.usecases

import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import javax.inject.Inject

class RefreshCharactersUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    operator fun invoke() = repository.requestForceRefresh()
}
