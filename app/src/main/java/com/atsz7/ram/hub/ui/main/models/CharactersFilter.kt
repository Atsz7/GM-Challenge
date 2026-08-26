package com.atsz7.ram.hub.ui.main.models

enum class CharactersFilter {
    ALL,
    FAVORITES;

    fun isFavorites() = this == FAVORITES
}
