package com.atsz7.ram.hub.domain.usecases

import androidx.paging.PagingData
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.domain.repositories.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    operator fun invoke(query: String = ""): Flow<PagingData<Character>> {
        return repository.getCharacters(query)
    }
}
