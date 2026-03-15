package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diegocunha.pokedex.core.mvi.BaseViewModel
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PokemonListViewModel(
    repository: PokemonRepository
) : BaseViewModel<PokemonListState, PokemonListIntent>(PokemonListState) {

    private val _effects = Channel<PokemonListEffect>(Channel.UNLIMITED)
    val effects: Flow<PokemonListEffect> = _effects.receiveAsFlow()

    val pagingFlow: Flow<PagingData<PokemonEntryResponse>> = flow {
        emitAll(repository.getPokemonList())
    }.cachedIn(viewModelScope)

    override fun processIntent(intent: PokemonListIntent) {
        when (intent) {
            is PokemonListIntent.SelectPokemon -> {
                val pokemonId = intent.url.trimEnd('/').substringAfterLast('/')
                viewModelScope.launch {
                    _effects.send(PokemonListEffect.NavigateToDetail(pokemonId))
                }
            }
        }
    }
}
