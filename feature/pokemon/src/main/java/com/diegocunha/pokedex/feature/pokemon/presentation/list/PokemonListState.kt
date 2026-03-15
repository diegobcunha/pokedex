package com.diegocunha.pokedex.feature.pokemon.presentation.list

import com.diegocunha.pokedex.core.mvi.MviIntent
import com.diegocunha.pokedex.core.mvi.MviState

object PokemonListState : MviState

sealed interface PokemonListIntent : MviIntent {
    data class SelectPokemon(val name: String, val url: String) : PokemonListIntent
}

sealed interface PokemonListEffect {
    data class NavigateToDetail(val pokemonId: String) : PokemonListEffect
    data object ShowErrorSnackbar : PokemonListEffect
}
