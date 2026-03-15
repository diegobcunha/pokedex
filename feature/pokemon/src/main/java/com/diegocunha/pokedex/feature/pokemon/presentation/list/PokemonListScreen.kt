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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import kotlinx.coroutines.flow.collectLatest

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
                    TextButton(onClick = { lazyPagingItems.retry() }) {
                        Text(text = "Retry")
                    }
                }
            }

            else -> {
                PokemonList(
                    lazyPagingItems = lazyPagingItems,
                    modifier = Modifier.padding(paddingValues),
                    onPokemonClick = { pokemon ->
                        viewModel.sendIntent(PokemonListIntent.SelectPokemon(id = pokemon.id))
                    }
                )
            }
        }
    }
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
            Text(
                text = pokemon.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPokemonClick(pokemon) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
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
