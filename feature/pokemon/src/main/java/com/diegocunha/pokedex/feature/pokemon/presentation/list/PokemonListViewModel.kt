package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.core.mvi.BaseViewModel
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.feature.pokemon.data.paging.PokemonPagingSource
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val apiService: PokemonApiService,
    private val dispatchers: DispatchersProvider
) : BaseViewModel<PokemonListState, PokemonListIntent>(PokemonListState) {

    private val _effects = Channel<PokemonListEffect>(Channel.UNLIMITED)
    val effects: Flow<PokemonListEffect> = _effects.receiveAsFlow()

    val pagingFlow: Flow<PagingData<PokemonEntry>> = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 20)
    ) {
        PokemonPagingSource(apiService, dispatchers)
    }.flow.cachedIn(viewModelScope)

    override fun processIntent(intent: PokemonListIntent) {
        when (intent) {
            is PokemonListIntent.SelectPokemon -> {
                viewModelScope.launch {
                    _effects.send(PokemonListEffect.NavigateToDetail(intent.id))
                }
            }
        }
    }
}
