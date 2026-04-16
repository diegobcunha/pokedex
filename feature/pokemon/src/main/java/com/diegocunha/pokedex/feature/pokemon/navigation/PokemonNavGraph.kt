package com.diegocunha.pokedex.feature.pokemon.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.PokemonDetailScreen
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.PokemonDetailViewModel
import com.diegocunha.pokedex.feature.pokemon.presentation.list.PokemonListScreen
import com.diegocunha.pokedex.feature.pokemon.presentation.list.PokemonListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EntryProviderScope<NavKey>.PokemonEntries(
    onNavigateToDetail: (pokemonId: String) -> Unit,
    onNavigateToPokemon: (pokemonId: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    entry<PokemonList> {
        val viewModel: PokemonListViewModel = koinViewModel()
        PokemonListScreen(
            viewModel = viewModel,
            onNavigateToDetail = onNavigateToDetail
        )
    }
    entry<PokemonDetail> { key ->
        val viewModel: PokemonDetailViewModel = koinViewModel(key = key.pokemonId) { parametersOf(key.pokemonId) }
        PokemonDetailScreen(
            viewModel = viewModel,
            onNavigateToPokemon = onNavigateToPokemon,
            onNavigateBack = onNavigateBack
        )
    }
}
