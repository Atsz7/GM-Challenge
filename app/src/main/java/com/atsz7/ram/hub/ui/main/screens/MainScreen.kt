package com.atsz7.ram.hub.ui.main.screens

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.atsz7.ram.hub.R
import com.atsz7.ram.hub.common.extensions.statusToBadge
import com.atsz7.ram.hub.common.ui.components.inputs.RamSearchView
import com.atsz7.ram.hub.common.ui.components.rows.RamBasicRow
import com.atsz7.ram.hub.common.ui.theme.RamHubTheme
import com.atsz7.ram.hub.common.utils.getShapeByIndex
import com.atsz7.ram.hub.core.domain.model.Character
import com.atsz7.ram.hub.extensions.label
import com.atsz7.ram.hub.ui.main.MainViewModel
import com.atsz7.ram.hub.ui.main.models.CharactersFilter

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {

    val characters = viewModel.characters.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    MainScreen(
        characters = characters,
        searchQuery = searchQuery,
        filter = filter,
        favoriteIds = favoriteIds,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onPullToRefresh = viewModel::onPullToRefresh,
        onToggleFavorite = viewModel::onToggleFavorite
    )
}

@Composable
private fun MainScreen(
    characters: LazyPagingItems<Character>,
    searchQuery: String,
    filter: CharactersFilter,
    favoriteIds: Set<Int>,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (CharactersFilter) -> Unit,
    onPullToRefresh: () -> Unit,
    onToggleFavorite: (Int) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // SearchView
            RamSearchView(
                modifier = Modifier.padding(
                    horizontal = RamHubTheme.dimens.mediumSize,
                    vertical = RamHubTheme.dimens.smallSize
                ),
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )

            // Characters filter (All / Favorites)
            CharactersFilterRow(
                modifier = Modifier.padding(
                    horizontal = RamHubTheme.dimens.mediumSize
                ),
                filter = filter,
                onFilterChange = onFilterChange
            )

            // Characters list
            CharactersListSection(
                modifier = Modifier.weight(1f),
                characters = characters,
                searchQuery = searchQuery,
                filter = filter,
                favoriteIds = favoriteIds,
                onPullToRefresh = onPullToRefresh,
                onToggleFavorite = onToggleFavorite
            )
        }
    }
}

@Composable
private fun CharactersFilterRow(
    filter: CharactersFilter,
    onFilterChange: (CharactersFilter) -> Unit,
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
                onClick = { onFilterChange(entry) }
            ) {
                Text(text = stringResource(id = entry.label))
            }
        }
    }
}

@Composable
private fun CharactersListSection(
    modifier: Modifier = Modifier,
    characters: LazyPagingItems<Character>,
    searchQuery: String,
    filter: CharactersFilter,
    favoriteIds: Set<Int>,
    onPullToRefresh: () -> Unit,
    onToggleFavorite: (Int) -> Unit
) {

    // Fresh LazyListState per query/filter resets scroll to top for new results.
    val listState = remember(searchQuery, filter) { LazyListState() }
    val isEmptyStateActive = searchQuery.isNotBlank() || filter == CharactersFilter.FAVORITES

    PullToRefreshBox(
        isRefreshing = characters.loadState.refresh is LoadState.Loading,
        onRefresh = {
            onPullToRefresh()
            characters.refresh()
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(all = RamHubTheme.dimens.mediumSize)
        ) {
            items(
                count = characters.itemCount,
                key = characters.itemKey { it.id }
            ) { index ->

                val character = characters[index]
                if (character != null) {

                    val shape = getShapeByIndex(
                        index = index,
                        size = characters.itemCount
                    )

                    RamBasicRow(
                        title = character.name,
                        subtitle = character.specie,
                        imageUrl = character.imageUrl,
                        badge = character.status.statusToBadge(),
                        shape = shape,
                        isFavorite = character.id in favoriteIds,
                        onFavoriteToggle = { onToggleFavorite(character.id) }
                    )

                    if (index < characters.itemCount) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(RamHubTheme.dimens.extraTinySize)
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
                                text = stringResource(R.string.no_search_results),
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
    }
}

@Composable
private fun CharactersErrorRow(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
