package com.atsz7.ram.hub.common.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private const val SHAPE_BIG_RADIUS = 16
private const val SHAPE_FLAT_RADIUS = 2

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
