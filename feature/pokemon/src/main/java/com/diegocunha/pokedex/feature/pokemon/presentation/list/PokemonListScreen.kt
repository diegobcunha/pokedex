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
import com.diegocunha.pokedex.feature.pokemon.domain.model.SearchFilter
import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType
import com.diegocunha.pokedex.feature.pokemon.presentation.list.components.PokemonSearchBar
import com.diegocunha.pokedex.feature.pokemon.presentation.list.components.SearchEmptyState
import com.diegocunha.pokedex.feature.pokemon.presentation.list.components.TypeFilterRow
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel,
    onNavigateToDetail: (pokemonId: String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val searchFilter by viewModel.searchFilter.collectAsState()
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
        searchFilter = searchFilter,
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = { pokemon ->
            viewModel.sendIntent(PokemonListIntent.SelectPokemon(id = pokemon.id))
        },
        onRetry = { viewModel.sendIntent(PokemonListIntent.Retry) },
        onQueryChange = { viewModel.sendIntent(PokemonListIntent.UpdateQuery(it)) },
        onTypeToggle = { viewModel.sendIntent(PokemonListIntent.ToggleTypeFilter(it)) },
        onClearFilters = { viewModel.sendIntent(PokemonListIntent.ClearFilters) }
    )
}

@Composable
private fun PokemonListScreenContent(
    state: PokemonListState,
    searchFilter: SearchFilter,
    lazyPagingItems: LazyPagingItems<PokemonEntry>,
    onPokemonClick: (PokemonEntry) -> Unit,
    onRetry: () -> Unit,
    onQueryChange: (String) -> Unit,
    onTypeToggle: (PokemonType) -> Unit,
    onClearFilters: () -> Unit
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    PokemonSearchBar(
                        query = searchFilter.query,
                        onQueryChange = onQueryChange
                    )
                    TypeFilterRow(
                        selectedTypes = searchFilter.selectedTypes,
                        onTypeToggle = onTypeToggle
                    )
                    PokemonList(
                        lazyPagingItems = lazyPagingItems,
                        searchFilter = searchFilter,
                        modifier = Modifier.weight(1f),
                        onPokemonClick = onPokemonClick,
                        onClearFilters = onClearFilters
                    )
                }
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

@Composable
private fun PokemonList(
    lazyPagingItems: LazyPagingItems<PokemonEntry>,
    searchFilter: SearchFilter,
    modifier: Modifier = Modifier,
    onPokemonClick: (PokemonEntry) -> Unit,
    onClearFilters: () -> Unit
) {
    val showEmptyState = lazyPagingItems.loadState.refresh !is LoadState.Loading
        && lazyPagingItems.itemCount == 0
        && searchFilter.isActive

    if (showEmptyState) {
        SearchEmptyState(onClearFilters = onClearFilters, modifier = modifier)
        return
    }

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

@Preview(showBackground = true, name = "PokemonListItem - with type and image")
@Composable
private fun PokemonListItemEnrichedPreview() {
    PokemonListItem(
        pokemon = PokemonEntry(id = "1", name = "Bulbasaur", types = persistentListOf("grass"), imageUrl = null),
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
        PokemonEntry(id = "1", name = "bulbasaur", types = persistentListOf("grass")),
        PokemonEntry(id = "2", name = "ivysaur", types = persistentListOf("grass", "poison")),
        PokemonEntry(id = "4", name = "charmander", types = persistentListOf("fire")),
    )
    val lazyPagingItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems()
    PokemonListScreenContent(
        state = PokemonListState.Success,
        searchFilter = SearchFilter(),
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = {},
        onRetry = {},
        onQueryChange = {},
        onTypeToggle = {},
        onClearFilters = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - Active filter empty state")
@Composable
private fun PokemonListScreenEmptyFilterPreview() {
    val lazyPagingItems = flowOf(PagingData.empty<PokemonEntry>()).collectAsLazyPagingItems()
    PokemonListScreenContent(
        state = PokemonListState.Success,
        searchFilter = SearchFilter(query = "pikachu", selectedTypes = persistentSetOf(PokemonType.FIRE)),
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = {},
        onRetry = {},
        onQueryChange = {},
        onTypeToggle = {},
        onClearFilters = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - Loading")
@Composable
private fun PokemonListScreenLoadingPreview() {
    val lazyPagingItems = flowOf(PagingData.empty<PokemonEntry>()).collectAsLazyPagingItems()
    PokemonListScreenContent(
        state = PokemonListState.Loading,
        searchFilter = SearchFilter(),
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = {},
        onRetry = {},
        onQueryChange = {},
        onTypeToggle = {},
        onClearFilters = {}
    )
}

@Preview(showBackground = true, name = "PokemonListScreen - Error")
@Composable
private fun PokemonListScreenErrorPreview() {
    val lazyPagingItems = flowOf(PagingData.empty<PokemonEntry>()).collectAsLazyPagingItems()
    PokemonListScreenContent(
        state = PokemonListState.Error(Exception("Network error")),
        searchFilter = SearchFilter(),
        lazyPagingItems = lazyPagingItems,
        onPokemonClick = {},
        onRetry = {},
        onQueryChange = {},
        onTypeToggle = {},
        onClearFilters = {}
    )
}
