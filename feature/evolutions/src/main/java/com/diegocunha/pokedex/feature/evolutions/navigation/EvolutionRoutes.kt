package com.diegocunha.pokedex.feature.evolutions.navigation

object EvolutionRoutes {
    const val EVOLUTION = "evolution/{pokemonId}"
    fun evolution(pokemonId: String) = "evolution/$pokemonId"
}
