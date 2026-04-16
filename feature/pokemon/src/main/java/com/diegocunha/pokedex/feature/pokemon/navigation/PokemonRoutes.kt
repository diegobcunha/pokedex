package com.diegocunha.pokedex.feature.pokemon.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object PokemonList : NavKey

@Serializable
data class PokemonDetail(val pokemonId: String) : NavKey
