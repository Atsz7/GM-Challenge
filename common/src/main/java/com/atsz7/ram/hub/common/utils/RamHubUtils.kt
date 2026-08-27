package com.atsz7.ram.hub.common.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private const val SHAPE_BIG_RADIUS = 16
private const val SHAPE_FLAT_RADIUS = 2

/**
 * Create and return a [RoundedCornerShape] based on the provided
 * [index] and [size].
 *
 * Rules:
 * The list has only one element, all corners are rounded.
 * It is the first element, the shape has only the top corners rounded.
 * It is the last element, the shape has only the bottom corners rounded.
 * In any other case, all corners are rounded with a flat radius.
 *
 * @return [RoundedCornerShape].
 */
fun getShapeByIndex(index: Int, size: Int): RoundedCornerShape {

    val bigRadius = SHAPE_BIG_RADIUS.dp
    val flatRadius = SHAPE_FLAT_RADIUS.dp

    return when {
        size == 1 -> RoundedCornerShape(bigRadius)
        index == 0 -> RoundedCornerShape(
            topStart = bigRadius,
            topEnd = bigRadius,
            bottomStart = flatRadius,
            bottomEnd = flatRadius
        )

        index == size - 1 -> RoundedCornerShape(
            topStart = flatRadius,
            topEnd = flatRadius,
            bottomStart = bigRadius,
            bottomEnd = bigRadius
        )

        else -> RoundedCornerShape(flatRadius)
    }
}
