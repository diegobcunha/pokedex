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
            repository.getPokemonDetail(pokemonId.toInt()).collect { resource ->
                when (resource) {
                    is Resource.Loading -> if (state.value !is PokemonDetailState.Success) {
                        updateState { PokemonDetailState.Loading }
                    }
                    is Resource.Success -> updateState { PokemonDetailState.Success(resource.data.toDomain()) }
                    is Resource.Error -> if (state.value !is PokemonDetailState.Success) {
                        updateState { PokemonDetailState.Error }
                    }
                }
            }
        }
    }

    override fun processIntent(intent: PokemonDetailIntent) {
        when (intent) {
            is PokemonDetailIntent.Retry -> loadDetail()
            is PokemonDetailIntent.NavigateToEvolution -> {
                val current = state.value
                if (current is PokemonDetailState.Success) {
                    viewModelScope.launch {
                        _effects.emit(PokemonDetailEffect.NavigateToEvolution(current.pokemon.id))
                    }
                }
            }
        }
    }
}
