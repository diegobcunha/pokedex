package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.br.diegocunha.pokedex.datasource.repository.PokemonUI

fun LazyListScope.makeLoadingContent(response: LazyPagingItems<PokemonUI>) {
    response.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                item {
                    Column(
                        modifier = Modifier.fillParentMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProgressIndicator()
                    }
                }
            }
            loadState.append is LoadState.Loading -> {
                item { ProgressIndicator() }
            }
            loadState.refresh is LoadState.Error -> {
                item {
                    ErrorView(
                        modifier = Modifier.fillParentMaxSize(),
                    ) {
                        retry()
                    }
                }
            }
            loadState.append is LoadState.Error -> {
                item {
                    makeError(
                        loadState = loadState.append
                    ) {
                        retry()
                    }
                }
            }
        }
    }
}

fun <T : Any> LazyGridScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyGridItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
    }
}