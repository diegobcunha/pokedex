package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.diegocunha.pokedex.coreui.theme.spacing
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel,
    onNavigateToDetail: (pokemonId: String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is PokemonListEffect.NavigateToDetail -> onNavigateToDetail(effect.pokemonId)
            }
        }
    }

    PokemonListScreenContent(
        state = state,
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = { pokemon ->
            viewModel.sendIntent(PokemonListIntent.SelectPokemon(id = pokemon.id))
        },
        onRetry = { viewModel.sendIntent(PokemonListIntent.Retry) }
    )
}

@Composable
private fun PokemonListScreenContent(
    state: PokemonListState,
    lazyPagingItems: LazyPagingItems<PokemonEntry>,
    onPokemonClick: (PokemonEntry) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold { paddingValues ->
        when (state) {
            is PokemonListState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PokemonListState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Failed to load Pokémon",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                        Button(onClick = onRetry) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            is PokemonListState.Success -> {
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
    val typeColor = PokemonType.fromName(pokemon.types.firstOrNull().orEmpty()).color
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs),
        colors = CardDefaults.cardColors(containerColor = typeColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.sm + MaterialTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm + MaterialTheme.spacing.xs))
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, name = "PokemonListItem - with type and image")
@Composable
private fun PokemonListItemEnrichedPreview() {
    PokemonListItem(
        pokemon = PokemonEntry(id = "1", name = "Bulbasaur", types = listOf("grass"), imageUrl = null),
        onClick = {}
    )
}

@Preview(showBackground = true, name = "PokemonListItem - fallback (no type, no image)")
@Composable
private fun PokemonListItemFallbackPreview() {
    PokemonListItem(
        pokemon = PokemonEntry(id = "1", name = "Bulbasaur"),
        onClick = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - List")
@Composable
private fun PokemonListScreenPreview() {
    val items = listOf(
        PokemonEntry(id = "1", name = "bulbasaur", types = listOf("grass")),
        PokemonEntry(id = "2", name = "ivysaur", types = listOf("grass", "poison")),
        PokemonEntry(id = "4", name = "charmander", types = listOf("fire")),
    )
    val lazyPagingItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems()
    PokemonListScreenContent(
        state = PokemonListState.Success,
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - Loading")
@Composable
private fun PokemonListScreenLoadingPreview() {
    val lazyPagingItems = flowOf(PagingData.empty<PokemonEntry>()).collectAsLazyPagingItems()
    PokemonListScreenContent(
        state = PokemonListState.Loading,
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - Error")
@Composable
private fun PokemonListScreenErrorPreview() {
    val lazyPagingItems = flowOf(PagingData.empty<PokemonEntry>()).collectAsLazyPagingItems()
    PokemonListScreenContent(
        state = PokemonListState.Error(Exception("Network error")),
        lazyPagingItems = lazyPagingItems,
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
        }

        if (lazyPagingItems.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
