package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import androidx.lifecycle.viewModelScope
import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.mvi.BaseViewModel
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.diegocunha.pokedex.feature.pokemon.domain.mapper.toDomain
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val pokemonId: String,
    private val repository: PokemonRepository
) : BaseViewModel<PokemonDetailState, PokemonDetailIntent>(PokemonDetailState.Loading) {

    private val _effects = Channel<PokemonDetailEffect>(Channel.UNLIMITED)
    val effects: Flow<PokemonDetailEffect> = _effects.receiveAsFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            updateState { PokemonDetailState.Loading }
            when (val result = repository.getPokemonDetail(pokemonId.toInt())) {
                is Resource.Success -> updateState { PokemonDetailState.Success(result.data.toDomain()) }
                is Resource.Error -> updateState { PokemonDetailState.Error }
                is Resource.Loading -> Unit
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
                        _effects.send(PokemonDetailEffect.NavigateToEvolution(current.pokemon.id))
                    }
                }
            }
        }
    }
}
