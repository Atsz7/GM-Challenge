package com.atsz7.ram.hub.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PageInfo(
    @SerializedName("count")
    val count: Int,
    @SerializedName("pages")
    val pages: Int,
    @SerializedName("next")
    val next: String? = null,
    @SerializedName("prev")
    val prev: String? = null
)
