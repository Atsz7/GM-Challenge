package com.atsz7.ram.hub.common.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class DateExtensionsTest {

    @Test
    fun `toFormattedDate formats a valid API timestamp`() {

        // Given
        val timestamp = "2017-11-05T11:53:44.737Z"

        // When
        val result = timestamp.toFormattedDate()

        // Then
        assertEquals("Nov 5, 2017", result)
    }

    @Test
    fun `toFormattedDate returns the original string when it doesn't match the expected format`() {

        // Given
        val invalidTimestamp = "not-a-date"

        // When
        val result = invalidTimestamp.toFormattedDate()

        // Then
        assertEquals(invalidTimestamp, result)
    }
}
