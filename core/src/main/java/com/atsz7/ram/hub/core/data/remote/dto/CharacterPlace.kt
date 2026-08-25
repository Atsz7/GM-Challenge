package com.atsz7.ram.hub.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterPlace(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)
