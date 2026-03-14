package com.diegocunha.pokedex.feature.pokemon.navigation

object PokemonRoutes {
    const val LIST = "pokemon/list"
    const val DETAIL = "pokemon/detail/{pokemonId}"
    fun detail(pokemonId: String) = "pokemon/detail/$pokemonId"
}
