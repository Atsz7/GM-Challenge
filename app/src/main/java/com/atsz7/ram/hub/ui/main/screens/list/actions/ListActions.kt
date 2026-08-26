package com.atsz7.ram.hub.ui.main.screens.list.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.atsz7.ram.hub.ui.main.screens.list.coordinator.ListCoordinator
import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter

data class ListActions(
    val onSearchQueryChange: (String) -> Unit = {},
    val onFilterChange: (CharactersFilter) -> Unit = {},
    val onPullToRefresh: () -> Unit = {},
    val onToggleFavorite: (Int) -> Unit = {},
    val onToggleDarkMode: () -> Unit = {},
    val onCharacterClick: (Int) -> Unit = {}
)

@Composable
fun rememberListActions(coordinator: ListCoordinator): ListActions =
    remember(coordinator) {
        ListActions(
            onSearchQueryChange = coordinator::onSearchQueryChange,
            onFilterChange = coordinator::onFilterChange,
            onPullToRefresh = coordinator::onPullToRefresh,
            onToggleFavorite = coordinator::onToggleFavorite,
            onToggleDarkMode = coordinator::onToggleDarkMode,
            onCharacterClick = coordinator::onCharacterClick
        )
    }
