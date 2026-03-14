package com.diegocunha.pokedex.feature.pokemon.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.PokemonDetailScreen
import com.diegocunha.pokedex.feature.pokemon.presentation.list.PokemonListScreen

fun NavGraphBuilder.pokemonGraph(
    onNavigateToDetail: (pokemonId: String) -> Unit,
    onNavigateToEvolution: (pokemonId: String) -> Unit
) {
    composable(PokemonRoutes.LIST) {
        PokemonListScreen(onNavigateToDetail = onNavigateToDetail)
    }
    composable(
        route = PokemonRoutes.DETAIL,
        arguments = listOf(navArgument("pokemonId") { type = NavType.StringType })
    ) { backStackEntry ->
        val pokemonId = backStackEntry.arguments?.getString("pokemonId").orEmpty()
        PokemonDetailScreen(
            pokemonId = pokemonId,
            onNavigateToEvolution = onNavigateToEvolution
        )
    }
}
