package com.diegocunha.pokedex.feature.evolutions.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.diegocunha.pokedex.feature.evolutions.presentation.EvolutionScreen

fun NavGraphBuilder.evolutionGraph() {
    composable(
        route = EvolutionRoutes.EVOLUTION,
        arguments = listOf(navArgument("pokemonId") { type = NavType.StringType })
    ) { backStackEntry ->
        val pokemonId = backStackEntry.arguments?.getString("pokemonId").orEmpty()
        EvolutionScreen(pokemonId = pokemonId)
    }
}
