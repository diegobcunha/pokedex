package com.diegocunha.pokedex.feature.pokemon.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.PokemonDetailScreen
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.PokemonDetailViewModel
import com.diegocunha.pokedex.feature.pokemon.presentation.list.PokemonListScreen
import com.diegocunha.pokedex.feature.pokemon.presentation.list.PokemonListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun EntryProviderBuilder<NavKey>.pokemonEntries(
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
        val viewModel: PokemonDetailViewModel = koinViewModel { parametersOf(key.pokemonId) }
        PokemonDetailScreen(
            viewModel = viewModel,
            onNavigateToPokemon = onNavigateToPokemon,
            onNavigateBack = onNavigateBack
        )
    }
}
