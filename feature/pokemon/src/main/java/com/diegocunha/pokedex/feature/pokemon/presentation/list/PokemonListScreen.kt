package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel,
    onNavigateToDetail: (pokemonId: String) -> Unit
) {
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is PokemonListEffect.NavigateToDetail -> onNavigateToDetail(effect.pokemonId)
                is PokemonListEffect.ShowErrorSnackbar -> snackbarHostState.showSnackbar("Failed to load more Pokémon")
            }
        }
    }

    LaunchedEffect(lazyPagingItems.loadState.append) {
        if (lazyPagingItems.loadState.append is LoadState.Error) {
            snackbarHostState.showSnackbar("Failed to load more Pokémon")
        }
    }

    PokemonListScreenContent(
        lazyPagingItems = lazyPagingItems,
        snackbarHostState = snackbarHostState,
        onPokemonClick = { pokemon ->
            viewModel.sendIntent(PokemonListIntent.SelectPokemon(id = pokemon.id))
        },
        onRetry = { lazyPagingItems.retry() }
    )
}

@Composable
private fun PokemonListScreenContent(
    lazyPagingItems: LazyPagingItems<PokemonEntry>,
    snackbarHostState: SnackbarHostState,
    onPokemonClick: (PokemonEntry) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                }
            }

            else -> {
                PokemonList(
                    lazyPagingItems = lazyPagingItems,
                    modifier = Modifier.padding(paddingValues),
                    onPokemonClick = onPokemonClick
                )
            }
        }
    }
}

@Composable
private fun PokemonListItem(
    pokemon: PokemonEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "#${pokemon.id} ${pokemon.name.replaceFirstChar { it.uppercase() }}",
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun PokemonListItemPreview() {
    PokemonListItem(
        pokemon = PokemonEntry(id = "1", name = "Bulbasaur"),
        onClick = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - List")
@Composable
private fun PokemonListScreenPreview() {
    val items = listOf(
        PokemonEntry(id = "1", name = "bulbasaur"),
        PokemonEntry(id = "2", name = "ivysaur"),
        PokemonEntry(id = "3", name = "venusaur"),
    )
    val lazyPagingItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems()
    PokemonListScreenContent(
        lazyPagingItems = lazyPagingItems,
        snackbarHostState = remember { SnackbarHostState() },
        onPokemonClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - Loading")
@Composable
private fun PokemonListScreenLoadingPreview() {
    val lazyPagingItems = flowOf(PagingData.empty<PokemonEntry>()).collectAsLazyPagingItems()
    PokemonListScreenContent(
        lazyPagingItems = lazyPagingItems,
        snackbarHostState = remember { SnackbarHostState() },
        onPokemonClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - Error")
@Composable
private fun PokemonListScreenErrorPreview() {
    val lazyPagingItems = flowOf(
        PagingData.empty<PokemonEntry>(
            sourceLoadStates = androidx.paging.LoadStates(
                refresh = LoadState.Error(Exception("Network error")),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()
    PokemonListScreenContent(
        lazyPagingItems = lazyPagingItems,
        snackbarHostState = remember { SnackbarHostState() },
        onPokemonClick = {},
        onRetry = {}
    )
}

@Composable
private fun PokemonList(
    lazyPagingItems: LazyPagingItems<PokemonEntry>,
    modifier: Modifier = Modifier,
    onPokemonClick: (PokemonEntry) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(lazyPagingItems.itemCount) { index ->
            val pokemon = lazyPagingItems[index] ?: return@items
            PokemonListItem(pokemon = pokemon, onClick = { onPokemonClick(pokemon) })
            HorizontalDivider()
        }

        if (lazyPagingItems.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
