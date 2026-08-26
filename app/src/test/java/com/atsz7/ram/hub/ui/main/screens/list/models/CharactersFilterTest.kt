package com.atsz7.ram.hub.ui.main.screens.list.models

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharactersFilterTest {

    @Test
    fun `isFavorites returns true when filter is FAVORITES`() {
        val filter = CharactersFilter.FAVORITES
        assertTrue(filter.isFavorites())
    }

    @Test
    fun `isFavorites returns false when filter is ALL`() {
        val filter = CharactersFilter.ALL
        assertFalse(filter.isFavorites())
    }
}
