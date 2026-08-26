package com.atsz7.ram.hub.common.extensions

import androidx.compose.ui.graphics.Color
import com.atsz7.ram.hub.common.R
import com.atsz7.ram.hub.common.ui.models.RamBasicBadge

private const val ALIVE_STATUS = "Alive"
private const val DEAD_STATUS = "Dead"

/**
 * Converts a [String] status to a [RamBasicBadge].
 * @return [RamBasicBadge].
 */
fun String.statusToBadge(): RamBasicBadge {
    return when (this) {
        ALIVE_STATUS -> RamBasicBadge.ALIVE
        DEAD_STATUS -> RamBasicBadge.DEAD
        else -> RamBasicBadge.UNKNOWN
    }
}

/**
 * Converts a [RamBasicBadge] to a string resource of [Int] type.
 */
val RamBasicBadge.label: Int
    get() = when (this) {
        RamBasicBadge.ALIVE -> R.string.badge_alive_label
        RamBasicBadge.DEAD -> R.string.badge_dead_label
        RamBasicBadge.UNKNOWN -> R.string.badge_unknown_label
    }

/**
 * Converts a [RamBasicBadge] to a [Color] type.
 */
val RamBasicBadge.color: Color
    get() = when (this) {
        RamBasicBadge.ALIVE -> Color(0xFF1B5E20)
        RamBasicBadge.DEAD -> Color(0xFF7A0C0C)
        RamBasicBadge.UNKNOWN -> Color(0xFF8C6D00)
    }
