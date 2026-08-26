package com.atsz7.ram.hub.ui.main.screens.detail.state

import com.atsz7.ram.hub.core.domain.model.Character

data class DetailScreenState(
    val character: Character? = null,
    val isFavorite: Boolean = false
)
