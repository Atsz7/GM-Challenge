package com.atsz7.ram.hub.domain.usecases

import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    operator fun invoke(): Flow<Set<Int>> = repository.observeFavoriteIds()
}
