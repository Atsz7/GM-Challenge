package com.atsz7.ram.hub.ui.main.screens.detail.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.atsz7.ram.hub.ui.main.screens.detail.coordinator.DetailCoordinator

data class DetailActions(
    val onToggleFavorite: () -> Unit = {},
    val onBackClick: () -> Unit = {}
)

@Composable
fun rememberDetailActions(coordinator: DetailCoordinator): DetailActions =
    remember(coordinator) {
        DetailActions(
            onToggleFavorite = coordinator::onToggleFavorite,
            onBackClick = coordinator::onBackClick
        )
    }
