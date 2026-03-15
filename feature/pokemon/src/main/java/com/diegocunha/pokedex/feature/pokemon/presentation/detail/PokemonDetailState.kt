package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import com.diegocunha.pokedex.core.mvi.MviIntent
import com.diegocunha.pokedex.core.mvi.MviState
import com.diegocunha.pokedex.feature.pokemon.domain.model.Pokemon

sealed interface PokemonDetailState : MviState {
    data object Loading : PokemonDetailState
    data class Success(val pokemon: Pokemon) : PokemonDetailState
    data object Error : PokemonDetailState
}

sealed interface PokemonDetailIntent : MviIntent {
    data object Retry : PokemonDetailIntent
    data object NavigateToEvolution : PokemonDetailIntent
}

sealed interface PokemonDetailEffect {
    data class NavigateToEvolution(val pokemonId: String) : PokemonDetailEffect
}
