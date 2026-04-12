package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import androidx.lifecycle.viewModelScope
import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.mvi.BaseViewModel
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.diegocunha.pokedex.feature.pokemon.domain.mapper.toDomain
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val pokemonId: String,
    private val repository: PokemonRepository
) : BaseViewModel<PokemonDetailState, PokemonDetailIntent>(PokemonDetailState.Loading) {

    private val _effects = MutableSharedFlow<PokemonDetailEffect>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects: Flow<PokemonDetailEffect> = _effects.asSharedFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            combine(
                repository.getPokemonDetail(pokemonId.toInt()),
                repository.getEvolutionData(pokemonId.toInt())
            ) { detail, evolution -> Pair(detail, evolution) }
                .collect { (detail, evolution) ->
                    when {
                        detail is Resource.Loading || evolution is Resource.Loading -> {
                            if (state.value !is PokemonDetailState.Success) {
                                updateState { PokemonDetailState.Loading }
                            }
                        }
                        detail is Resource.Error || evolution is Resource.Error -> {
                            if (state.value !is PokemonDetailState.Success) {
                                updateState { PokemonDetailState.Error }
                            }
                        }
                        detail is Resource.Success && evolution is Resource.Success -> {
                            updateState {
                                PokemonDetailState.Success(
                                    pokemon = detail.data.toDomain(),
                                    evolution = evolution.data.toDomain()
                                )
                            }
                        }
                    }
                }
        }
    }

    override fun processIntent(intent: PokemonDetailIntent) {
        when (intent) {
            is PokemonDetailIntent.Retry -> loadDetail()
            is PokemonDetailIntent.NavigateToPokemon -> {
                viewModelScope.launch {
                    _effects.emit(PokemonDetailEffect.NavigateToPokemon(intent.pokemonId))
                }
            }
        }
    }
}
