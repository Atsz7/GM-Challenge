package com.atsz7.ram.hub.common.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RamHubDimens(
    val extraTinySize: Dp = 2.dp,
    val tinySize: Dp = 4.dp,
    val smallSize: Dp = 8.dp,
    val mediumSize: Dp = 16.dp,
    val largeSize: Dp = 24.dp,
    val extraLargeSize: Dp = 32.dp,
    val extraExtraLargeSize: Dp = 48.dp
)

val LocalRamHubDimens = staticCompositionLocalOf { RamHubDimens() }
