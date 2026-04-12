package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import com.diegocunha.pokedex.core.mvi.MviIntent
import com.diegocunha.pokedex.core.mvi.MviState
import com.diegocunha.pokedex.feature.pokemon.domain.model.EvolutionChain
import com.diegocunha.pokedex.feature.pokemon.domain.model.Pokemon

sealed interface PokemonDetailState : MviState {
    data object Loading : PokemonDetailState
    data class Success(val pokemon: Pokemon, val evolution: EvolutionChain) : PokemonDetailState
    data object Error : PokemonDetailState
}

sealed interface PokemonDetailIntent : MviIntent {
    data object Retry : PokemonDetailIntent
    data class NavigateToPokemon(val pokemonId: String) : PokemonDetailIntent
}

sealed interface PokemonDetailEffect {
    data class NavigateToPokemon(val pokemonId: String) : PokemonDetailEffect
}
