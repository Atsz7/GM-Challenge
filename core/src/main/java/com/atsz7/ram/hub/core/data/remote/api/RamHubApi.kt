package com.atsz7.ram.hub.core.data.remote.api

import com.atsz7.ram.hub.core.data.remote.dto.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RamHubApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int
    ): CharacterResponse
}
