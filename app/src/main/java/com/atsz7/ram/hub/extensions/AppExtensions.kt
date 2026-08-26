package com.atsz7.ram.hub.extensions

import com.atsz7.ram.hub.R
import com.atsz7.ram.hub.ui.main.models.CharactersFilter

/**
 * Converts a [CharactersFilter] to string resource of [Int] type.
 */
val CharactersFilter.label: Int
    get() = when (this) {
        CharactersFilter.ALL -> R.string.filter_all_label
        CharactersFilter.FAVORITES -> R.string.filter_favorites_label
    }
