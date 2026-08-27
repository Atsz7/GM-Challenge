package com.atsz7.ram.hub.extensions

import com.atsz7.ram.hub.R
import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class AppExtensionsTest {

    @Test
    fun `label returns the all-characters string resource for the ALL filter`() {
        assertEquals(R.string.filter_all_label, CharactersFilter.ALL.label)
    }

    @Test
    fun `label returns the favorites string resource for the FAVORITES filter`() {
        assertEquals(R.string.filter_favorites_label, CharactersFilter.FAVORITES.label)
    }
}
