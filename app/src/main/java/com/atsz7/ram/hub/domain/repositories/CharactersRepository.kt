package com.atsz7.ram.hub.domain.repositories

import androidx.paging.PagingData
import com.atsz7.ram.hub.core.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    fun getCharacters(query: String = ""): Flow<PagingData<Character>>
    fun requestForceRefresh()
}
