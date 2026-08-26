package com.atsz7.ram.hub.ui.main.navigation

import kotlinx.serialization.Serializable

sealed interface MainRoute {

    @Serializable
    data object Characters : MainRoute

    @Serializable
    data class Detail(val characterId: Int) : MainRoute
}
