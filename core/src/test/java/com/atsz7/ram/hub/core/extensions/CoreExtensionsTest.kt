package com.atsz7.ram.hub.core.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class CoreExtensionsTest {

    @Test
    fun `normalizeForSearch strips diacritics and lowercases`() {

        // Given
        val name = "Rick Sánchez"

        // When
        val result = name.normalizeForSearch()

        // Then
        assertEquals("rick sanchez", result)
    }

    @Test
    fun `normalizeForSearch trims leading and trailing whitespace`() {

        // Given
        val name = "  Morty  "

        // When
        val result = name.normalizeForSearch()

        // Then
        assertEquals("morty", result)
    }

    @Test
    fun `normalizeForSearch keeps internal punctuation and spacing untouched`() {

        // Given
        val name = "Mr. Poopybutthole!"

        // When
        val result = name.normalizeForSearch()

        // Then
        assertEquals("mr. poopybutthole!", result)
    }

    @Test
    fun `normalizeForSearch on a blank string returns an empty string`() {

        // Given
        val name = "   "

        // When
        val result = name.normalizeForSearch()

        // Then
        assertEquals("", result)
    }

    @Test
    fun `normalizeForSearch is idempotent`() {

        // Given
        val name = "Rick Sánchez"

        // When
        val result = name.normalizeForSearch().normalizeForSearch()

        // Then
        assertEquals(name.normalizeForSearch(), result)
    }

    @Test
    fun `toNormalizedSearchTerm collapses punctuation and extra spaces into single-space terms`() {

        // Given
        val term = "  Rick  Sán!"

        // When
        val result = term.toNormalizedSearchTerm()

        // Then
        assertEquals("rick san", result)
    }

    @Test
    fun `toNormalizedSearchTerm drops purely non-alphanumeric input`() {

        // Given
        val term = "!!!"

        // When
        val result = term.toNormalizedSearchTerm()

        // Then
        assertEquals("", result)
    }

    @Test
    fun `toNormalizedSearchTerm on a blank string returns an empty string`() {

        // Given
        val term = "   "

        // When
        val result = term.toNormalizedSearchTerm()

        // Then
        assertEquals("", result)
    }

    @Test
    fun `toNormalizedSearchTerm keeps alphanumeric characters from different scripts`() {

        // Given
        val term = "Beth123"

        // When
        val result = term.toNormalizedSearchTerm()

        // Then
        assertEquals("beth123", result)
    }
}
