package com.atsz7.ram.hub.ui.main.screens.list.state

import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter

data class ListScreenState(
    val searchQuery: String = "",
    val filter: CharactersFilter = CharactersFilter.ALL,
    val favoriteIds: Set<Int> = emptySet()
)
