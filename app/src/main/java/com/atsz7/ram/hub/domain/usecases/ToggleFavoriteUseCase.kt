package com.atsz7.ram.hub.domain.usecases

import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    suspend operator fun invoke(id: Int, isFavorite: Boolean) {
        repository.toggleFavorite(id, isFavorite)
    }
}
