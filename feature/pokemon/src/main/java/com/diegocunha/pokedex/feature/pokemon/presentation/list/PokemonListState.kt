package com.diegocunha.pokedex.feature.pokemon.presentation.list

import com.diegocunha.pokedex.core.mvi.MviIntent
import com.diegocunha.pokedex.core.mvi.MviState
import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType

sealed class PokemonListState : MviState {
    object Loading : PokemonListState()
    object Success : PokemonListState()
    data class Error(val exception: Throwable) : PokemonListState()
}

sealed interface PokemonListIntent : MviIntent {
    data class SelectPokemon(val id: String) : PokemonListIntent
    data object Retry : PokemonListIntent
    data class UpdateQuery(val query: String) : PokemonListIntent
    data class ToggleTypeFilter(val type: PokemonType) : PokemonListIntent
    data object ClearFilters : PokemonListIntent
}

sealed interface PokemonListEffect {
    data class NavigateToDetail(val pokemonId: String) : PokemonListEffect
}
