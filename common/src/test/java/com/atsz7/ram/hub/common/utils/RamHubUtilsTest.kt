package com.atsz7.ram.hub.common.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class RamHubUtilsTest {

    private val bigRadius = 16.dp
    private val flatRadius = 2.dp

    @Test
    fun `getShapeByIndex rounds every corner when the list has a single element`() {

        // When
        val result = getShapeByIndex(index = 0, size = 1)

        // Then
        assertEquals(RoundedCornerShape(bigRadius), result)
    }

    @Test
    fun `getShapeByIndex rounds only the top corners for the first element`() {

        // When
        val result = getShapeByIndex(index = 0, size = 3)

        // Then
        assertEquals(
            RoundedCornerShape(
                topStart = bigRadius,
                topEnd = bigRadius,
                bottomStart = flatRadius,
                bottomEnd = flatRadius
            ),
            result
        )
    }

    @Test
    fun `getShapeByIndex rounds only the bottom corners for the last element`() {

        // When
        val result = getShapeByIndex(index = 2, size = 3)

        // Then
        assertEquals(
            RoundedCornerShape(
                topStart = flatRadius,
                topEnd = flatRadius,
                bottomStart = bigRadius,
                bottomEnd = bigRadius
            ),
            result
        )
    }

    @Test
    fun `getShapeByIndex uses a flat radius on every corner for a middle element`() {

        // When
        val result = getShapeByIndex(index = 1, size = 3)

        // Then
        assertEquals(RoundedCornerShape(flatRadius), result)
    }

    @Test
    fun `getShapeByIndex treats a two-element list as first and last, never middle`() {

        // When
        val first = getShapeByIndex(index = 0, size = 2)
        val last = getShapeByIndex(index = 1, size = 2)

        // Then
        assertEquals(
            RoundedCornerShape(
                topStart = bigRadius,
                topEnd = bigRadius,
                bottomStart = flatRadius,
                bottomEnd = flatRadius
            ),
            first
        )
        assertEquals(
            RoundedCornerShape(
                topStart = flatRadius,
                topEnd = flatRadius,
                bottomStart = bigRadius,
                bottomEnd = bigRadius
            ),
            last
        )
    }
}
