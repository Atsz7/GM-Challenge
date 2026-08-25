package com.atsz7.ram.hub.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterResult(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("species")
    val specie: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("origin")
    val origin: CharacterPlace,
    @SerializedName("location")
    val location: CharacterPlace,
    @SerializedName("image")
    val image: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("created")
    val created: String
)
