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
import com.atsz7.ram.hub.ui.main.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {

    val characters = viewModel.characters.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()

    MainScreen(
        characters = characters,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onPullToRefresh = viewModel::onPullToRefresh
    )
}

@Composable
private fun MainScreen(
    characters: LazyPagingItems<Character>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPullToRefresh: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            RamSearchView(
                modifier = Modifier.padding(
                    horizontal = RamHubTheme.dimens.mediumSize,
                    vertical = RamHubTheme.dimens.smallSize
                ),
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )
            CharactersListSection(
                modifier = Modifier.weight(1f),
                characters = characters,
                searchQuery = searchQuery,
                onPullToRefresh = onPullToRefresh
            )
        }
    }
}

@Composable
private fun CharactersListSection(
    modifier: Modifier = Modifier,
    characters: LazyPagingItems<Character>,
    searchQuery: String,
    onPullToRefresh: () -> Unit
) {

    // Fresh LazyListState per query resets scroll to top for new results.
    val listState = remember(searchQuery) { LazyListState() }
    val isSearchActive = searchQuery.isNotBlank()

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
                        shape = shape
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
                        && isSearchActive
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
