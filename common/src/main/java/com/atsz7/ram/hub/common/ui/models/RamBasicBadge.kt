package com.atsz7.ram.hub.common.ui.models

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.atsz7.ram.hub.common.R

enum class RamBasicBadge(
    @StringRes val label: Int, val color: Color
) {
    ALIVE(
        label = R.string.badge_alive_label,
        color = Color(0xFF1B5E20)
    ),
    DEAD(
        label = R.string.badge_dead_label,
        color = Color(0xFF7A0C0C)
    ),
    UNKNOWN(
        label = R.string.badge_unknown_label,
        color = Color(0xFF8C6D00)
    )
}
