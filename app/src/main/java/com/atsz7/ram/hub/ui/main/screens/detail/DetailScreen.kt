package com.atsz7.ram.hub.ui.main.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.atsz7.ram.hub.R
import com.atsz7.ram.hub.common.extensions.statusToBadge
import com.atsz7.ram.hub.common.extensions.toFormattedDate
import com.atsz7.ram.hub.common.ui.components.bars.RamTopBar
import com.atsz7.ram.hub.common.ui.components.buttons.RamCircleIconButton
import com.atsz7.ram.hub.common.ui.components.rows.StatusBadge
import com.atsz7.ram.hub.common.ui.theme.RamHubTheme
import com.atsz7.ram.hub.common.utils.getShapeByIndex
import com.atsz7.ram.hub.domain.model.Character
import com.atsz7.ram.hub.ui.main.screens.detail.actions.DetailActions
import com.atsz7.ram.hub.ui.main.screens.detail.actions.rememberDetailActions
import com.atsz7.ram.hub.ui.main.screens.detail.coordinator.DetailCoordinator
import com.atsz7.ram.hub.ui.main.screens.detail.state.DetailScreenState
import com.atsz7.ram.hub.common.R as CommonR

private val DetailInfoShape = getShapeByIndex(index = 0, size = 1)

@Composable
fun DetailScreen(coordinator: DetailCoordinator) {

    val state by coordinator.uiState.collectAsStateWithLifecycle()
    val actions = rememberDetailActions(coordinator)

    DetailContent(state = state, actions = actions)
}

@Composable
private fun DetailContent(
    state: DetailScreenState,
    actions: DetailActions
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(RamHubTheme.dimens.mediumSize)
        ) {
            DetailTopBar(
                createdAt = state.character?.createdAt,
                isFavorite = state.isFavorite,
                actions = actions
            )

            if (state.character != null) {
                CharacterDetailSection(
                    modifier = Modifier.padding(top = RamHubTheme.dimens.largeSize),
                    character = state.character
                )
            }
        }
    }
}

@Composable
private fun DetailTopBar(
    createdAt: String?,
    isFavorite: Boolean,
    actions: DetailActions,
    modifier: Modifier = Modifier
) {
    RamTopBar(
        modifier = modifier,
        title = stringResource(R.string.created_label),
        subtitle = createdAt?.toFormattedDate(),
        navigationIcon = {
            RamCircleIconButton(
                onClick = actions.onBackClick,
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.back_cd),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        actionIcon = {
            RamCircleIconButton(
                onClick = actions.onToggleFavorite,
                icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = stringResource(
                    if (isFavorite) CommonR.string.remove_favorite_cd else CommonR.string.add_favorite_cd
                ),
                tint = if (isFavorite) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    )
}

@Composable
private fun CharacterDetailSection(
    character: Character,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = DetailInfoShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        CharacterDetailContent(character = character)
    }
}

@Composable
private fun CharacterDetailContent(
    character: Character,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(RamHubTheme.dimens.mediumSize)
    ) {
        CharacterDetailPhoto(character = character)

        Column(modifier = Modifier.padding(start = RamHubTheme.dimens.mediumSize)) {
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = Modifier.padding(top = RamHubTheme.dimens.extraTinySize),
                text = character.specie,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CharacterDetailLabeledText(
                modifier = Modifier.padding(top = RamHubTheme.dimens.smallSize),
                label = stringResource(R.string.gender_label),
                value = character.gender
            )
            CharacterDetailLabeledText(
                modifier = Modifier.padding(top = RamHubTheme.dimens.extraTinySize),
                label = stringResource(R.string.origin_label),
                value = character.originName
            )
            CharacterDetailLabeledText(
                modifier = Modifier.padding(top = RamHubTheme.dimens.extraTinySize),
                label = stringResource(R.string.location_label),
                value = character.locationName
            )
            StatusBadge(
                modifier = Modifier.padding(top = RamHubTheme.dimens.smallSize),
                badge = character.status.statusToBadge()
            )
        }
    }
}

@Composable
private fun CharacterDetailPhoto(
    character: Character,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(RamHubTheme.dimens.extraExtraLargeSize * 2)
            .clip(RoundedCornerShape(RamHubTheme.dimens.mediumSize))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(RamHubTheme.dimens.mediumSize)),
            model = character.imageUrl,
            contentDescription = character.name,
            contentScale = ContentScale.Crop,
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(RamHubTheme.dimens.extraExtraLargeSize),
                        strokeWidth = RamHubTheme.dimens.extraTinySize,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            error = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = stringResource(CommonR.string.broken_image_cd),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(RamHubTheme.dimens.extraExtraLargeSize)
                    )
                }
            }
        )
    }
}

@Composable
private fun CharacterDetailLabeledText(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$label: ")
            }
            append(value)
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailContentPreview() {
    RamHubTheme {
        DetailContent(
            state = DetailScreenState(
                character = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    specie = "Human",
                    gender = "Male",
                    originName = "Earth (C-137)",
                    locationName = "Citadel of Ricks",
                    imageUrl = "",
                    createdAt = "2017-11-04T18:48:46.250Z"
                ),
                isFavorite = true
            ),
            actions = DetailActions()
        )
    }
}
