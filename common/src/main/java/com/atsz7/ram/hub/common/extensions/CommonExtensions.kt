package com.atsz7.ram.hub.common.extensions

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
