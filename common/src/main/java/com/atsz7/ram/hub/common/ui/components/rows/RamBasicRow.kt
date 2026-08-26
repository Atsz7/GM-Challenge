package com.atsz7.ram.hub.common.ui.components.rows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.atsz7.ram.hub.common.R
import com.atsz7.ram.hub.common.extensions.color
import com.atsz7.ram.hub.common.extensions.label
import com.atsz7.ram.hub.common.ui.models.RamBasicBadge
import com.atsz7.ram.hub.common.ui.theme.RamHubTheme

@Composable
fun RamBasicRow(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    imageUrl: String,
    badge: RamBasicBadge,
    shape: Shape,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = RamHubTheme.dimens.mediumSize,
                    vertical = RamHubTheme.dimens.smallSize
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(RamHubTheme.dimens.extraExtraLargeSize)
                    .clip(RoundedCornerShape(RamHubTheme.dimens.mediumSize))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(RamHubTheme.dimens.mediumSize)),
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(RamHubTheme.dimens.largeSize),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    error = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(RamHubTheme.dimens.largeSize)
                            )
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = RamHubTheme.dimens.smallSize)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusBadge(badge = badge)
            }

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.remove_favorite_cd else R.string.add_favorite_cd
                    ),
                    tint = if (isFavorite) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    badge: RamBasicBadge,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(RamHubTheme.dimens.smallSize))
            .background(badge.color)
            .padding(all = RamHubTheme.dimens.tinySize)
    ) {
        Text(
            text = stringResource(badge.label),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
