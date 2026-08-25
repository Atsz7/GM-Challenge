package com.atsz7.ram.hub.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    @SerializedName("info")
    val info: PageInfo,
    @SerializedName("results")
    val results: List<CharacterResult>
)
