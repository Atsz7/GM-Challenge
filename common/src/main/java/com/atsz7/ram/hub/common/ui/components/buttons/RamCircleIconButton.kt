package com.atsz7.ram.hub.common.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun RamCircleIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape
        ),
        onClick = onClick
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }
}
