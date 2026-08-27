package com.atsz7.ram.hub.ui.main.screens.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.atsz7.ram.hub.R
import com.atsz7.ram.hub.common.extensions.statusToBadge
import com.atsz7.ram.hub.common.ui.components.buttons.RamCircleIconButton
import com.atsz7.ram.hub.common.ui.components.inputs.RamSearchView
import com.atsz7.ram.hub.common.ui.components.rows.RamBasicRow
import com.atsz7.ram.hub.common.ui.theme.RamHubTheme
import com.atsz7.ram.hub.common.utils.getShapeByIndex
import com.atsz7.ram.hub.domain.model.Character
import com.atsz7.ram.hub.common.ui.components.bars.RamTopBar
import com.atsz7.ram.hub.extensions.label
import com.atsz7.ram.hub.ui.main.screens.list.actions.ListActions
import com.atsz7.ram.hub.ui.main.screens.list.actions.rememberListActions
import com.atsz7.ram.hub.ui.main.screens.list.coordinator.ListCoordinator
import com.atsz7.ram.hub.ui.main.screens.list.models.CharactersFilter
import com.atsz7.ram.hub.ui.main.screens.list.state.ListScreenState
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import com.atsz7.ram.hub.common.R as CommonR

@Composable
fun ListScreen(coordinator: ListCoordinator) {

    val state by coordinator.uiState.collectAsStateWithLifecycle()
    val characters = coordinator.characters.collectAsLazyPagingItems()
    val actions = rememberListActions(coordinator)

    ListContent(
        state = state,
        characters = characters,
        actions = actions
    )
}

@Composable
private fun ListContent(
    state: ListScreenState,
    characters: LazyPagingItems<Character>,
    actions: ListActions
) {

    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    LaunchedEffect(characters.loadState.refresh) {
        if (characters.loadState.refresh is LoadState.NotLoading) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { ListFabSection(listState) }
    ) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {

            // Top bar with theme toggle
            ListTopBar(
                isDarkMode = state.isDarkMode,
                actions = actions
            )

            val isRefreshing = characters.loadState.refresh is LoadState.Loading
            val pullToRefreshEnabled = !state.filter.isFavorites()
            val pullToRefreshState = rememberPullToRefreshState()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullToRefresh(
                        isRefreshing = isRefreshing,
                        state = pullToRefreshState,
                        enabled = pullToRefreshEnabled,
                        onRefresh = {
                            actions.onPullToRefresh()
                            characters.refresh()
                        }
                    )
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {

                    // SearchView
                    item {
                        RamSearchView(
                            modifier = Modifier.padding(
                                horizontal = RamHubTheme.dimens.mediumSize,
                                vertical = RamHubTheme.dimens.smallSize
                            ),
                            query = state.searchQuery,
                            onQueryChange = actions.onSearchQueryChange
                        )
                    }

                    // Characters filter (All / Favorites)
                    item {
                        CharactersFilterRow(
                            modifier = Modifier.padding(
                                horizontal = RamHubTheme.dimens.mediumSize
                            ),
                            filter = state.filter,
                            actions = actions
                        )
                    }

                    // Characters list
                    charactersListSection(
                        characters = characters,
                        isEmptyStateActive = state.searchQuery.isNotBlank()
                                || state.filter.isFavorites(),
                        isFavoritesFilter = state.filter.isFavorites(),
                        favoriteIds = state.favoriteIds,
                        actions = actions
                    )
                }

                if (pullToRefreshEnabled) {
                    PullToRefreshDefaults.Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = isRefreshing,
                        state = pullToRefreshState
                    )
                }
            }
        }
    }
}

@Composable
private fun ListTopBar(
    isDarkMode: Boolean,
    actions: ListActions,
    modifier: Modifier = Modifier
) {
    RamTopBar(
        modifier = modifier
            .padding(horizontal = RamHubTheme.dimens.mediumSize)
            .padding(top = RamHubTheme.dimens.mediumSize),
        title = stringResource(R.string.app_name),
        subtitle = stringResource(R.string.characters_subtitle),
        actionIcon = {
            RamCircleIconButton(
                onClick = actions.onToggleDarkMode,
                icon = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = stringResource(
                    if (isDarkMode) CommonR.string.light_mode_cd else CommonR.string.dark_mode_cd
                ),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
private fun CharactersFilterRow(
    filter: CharactersFilter,
    actions: ListActions,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = RamHubTheme.dimens.smallSize)
    ) {
        CharactersFilter.entries.forEachIndexed { index, entry ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = CharactersFilter.entries.size
                ),
                selected = filter == entry,
                onClick = { actions.onFilterChange(entry) }
            ) {
                Text(text = stringResource(id = entry.label))
            }
        }
    }
}

private fun LazyListScope.charactersListSection(
    characters: LazyPagingItems<Character>,
    isEmptyStateActive: Boolean,
    isFavoritesFilter: Boolean,
    favoriteIds: ImmutableSet<Int>,
    actions: ListActions
) {

    items(
        count = characters.itemCount,
        key = characters.itemKey { it.id }
    ) { index ->

        val character = characters[index]
        if (character != null) {

            val shape = remember(index, characters.itemCount) {
                getShapeByIndex(
                    index = index,
                    size = characters.itemCount
                )
            }

            RamBasicRow(
                modifier = Modifier.padding(horizontal = RamHubTheme.dimens.mediumSize),
                title = character.name,
                subtitle = character.specie,
                imageUrl = character.imageUrl,
                badge = character.status.statusToBadge(),
                shape = shape,
                isFavorite = character.id in favoriteIds,
                onFavoriteToggle = { actions.onToggleFavorite(character.id) },
                onClick = { actions.onCharacterClick(character.id) }
            )

            if (index < characters.itemCount - 1) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(RamHubTheme.dimens.extraTinySize)
                        .padding(horizontal = RamHubTheme.dimens.mediumSize)
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }
    }

    when {
        characters.itemCount == 0
                && isEmptyStateActive
                && characters.loadState.refresh is LoadState.NotLoading -> {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = RamHubTheme.dimens.extraLargeSize),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(
                            if (isFavoritesFilter) {
                                R.string.no_favorites_found
                            } else {
                                R.string.no_search_results
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        characters.loadState.append is LoadState.Loading
                && characters.itemCount > 0 -> {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        characters.loadState.refresh is LoadState.Error
                || characters.loadState.append is LoadState.Error -> {
            item {
                CharactersErrorRow(
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = { characters.retry() }
                )
            }
        }
    }
}

@Composable
private fun CharactersErrorRow(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(top = RamHubTheme.dimens.extraLargeSize),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.characters_load_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry))
                Spacer(modifier = Modifier.width(RamHubTheme.dimens.tinySize))
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.retry)
                )
            }
        }
    }
}

@Composable
private fun ListFabSection(listState: LazyListState) {

    val showFab by remember(listState) {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }
    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = showFab,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = stringResource(R.string.scroll_to_top_cd)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListContentPreview() {
    val previewCharacters = List(5) { index ->
        Character(
            id = index,
            name = "Rick Sanchez",
            status = "Alive",
            specie = "Human",
            gender = "Male",
            originName = "Earth (C-137)",
            locationName = "Citadel of Ricks",
            imageUrl = "",
            createdAt = "2017-11-04T18:48:46.250Z"
        )
    }

    RamHubTheme {
        ListContent(
            state = ListScreenState(favoriteIds = persistentSetOf(0)),
            characters = flowOf(PagingData.from(previewCharacters)).collectAsLazyPagingItems(),
            actions = ListActions()
        )
    }
}
