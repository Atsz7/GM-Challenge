package com.atsz7.ram.hub.ui.main.screens.list.models

enum class CharactersFilter {
    ALL,
    FAVORITES;

    fun isFavorites() = this == FAVORITES
}
