package com.atsz7.ram.hub.common.extensions

import androidx.compose.ui.graphics.Color
import com.atsz7.ram.hub.common.R
import com.atsz7.ram.hub.common.ui.models.RamBasicBadge
import org.junit.Assert.assertEquals
import org.junit.Test

class CommonExtensionsTest {

    @Test
    fun `statusToBadge maps Alive to the ALIVE badge`() {

        // Given
        val status = "Alive"

        // When
        val result = status.statusToBadge()

        // Then
        assertEquals(RamBasicBadge.ALIVE, result)
    }

    @Test
    fun `statusToBadge maps Dead to the DEAD badge`() {

        // Given
        val status = "Dead"

        // When
        val result = status.statusToBadge()

        // Then
        assertEquals(RamBasicBadge.DEAD, result)
    }

    @Test
    fun `statusToBadge maps an unrecognized status to the UNKNOWN badge`() {

        // Given
        val status = "unknown"

        // When
        val result = status.statusToBadge()

        // Then
        assertEquals(RamBasicBadge.UNKNOWN, result)
    }

    @Test
    fun `statusToBadge is case-sensitive and falls back to UNKNOWN`() {

        // Given
        val status = "alive"

        // When
        val result = status.statusToBadge()

        // Then
        assertEquals(RamBasicBadge.UNKNOWN, result)
    }

    @Test
    fun `label returns the alive string resource for the ALIVE badge`() {
        assertEquals(R.string.badge_alive_label, RamBasicBadge.ALIVE.label)
    }

    @Test
    fun `label returns the dead string resource for the DEAD badge`() {
        assertEquals(R.string.badge_dead_label, RamBasicBadge.DEAD.label)
    }

    @Test
    fun `label returns the unknown string resource for the UNKNOWN badge`() {
        assertEquals(R.string.badge_unknown_label, RamBasicBadge.UNKNOWN.label)
    }

    @Test
    fun `color returns the expected color for the ALIVE badge`() {
        assertEquals(Color(0xFF1B5E20), RamBasicBadge.ALIVE.color)
    }

    @Test
    fun `color returns the expected color for the DEAD badge`() {
        assertEquals(Color(0xFF7A0C0C), RamBasicBadge.DEAD.color)
    }

    @Test
    fun `color returns the expected color for the UNKNOWN badge`() {
        assertEquals(Color(0xFF8C6D00), RamBasicBadge.UNKNOWN.color)
    }
}
