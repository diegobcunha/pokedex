package com.diegocunha.pokedex.feature.pokemon.navigation

import androidx.navigation3.runtime.NavKey

data object PokemonList : NavKey
data class PokemonDetail(val pokemonId: String) : NavKey
