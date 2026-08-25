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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.atsz7.ram.hub.R
import com.atsz7.ram.hub.common.extensions.statusToBadge
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
    MainScreen(
        characters = characters,
        onPullToRefresh = viewModel::onPullToRefresh
    )
}

@Composable
private fun MainScreen(
    characters: LazyPagingItems<Character>,
    onPullToRefresh: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        CharactersListSection(
            modifier = Modifier.padding(innerPadding),
            characters = characters,
            onPullToRefresh = onPullToRefresh
        )
    }
}

@Composable
private fun CharactersListSection(
    modifier: Modifier = Modifier,
    characters: LazyPagingItems<Character>,
    onPullToRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = characters.loadState.refresh is LoadState.Loading,
        onRefresh = {
            onPullToRefresh()
            characters.refresh()
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
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
