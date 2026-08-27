package com.atsz7.ram.hub.ui.main.screens.list.state

import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class ListScreenState(
    val searchQuery: String = "",
    val filter: CharactersFilter = CharactersFilter.ALL,
    val favoriteIds: ImmutableSet<Int> = persistentSetOf(),
    val isDarkMode: Boolean = false
)
