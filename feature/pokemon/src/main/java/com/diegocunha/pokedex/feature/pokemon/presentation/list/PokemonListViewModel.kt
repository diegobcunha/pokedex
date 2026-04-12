package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.core.mvi.BaseViewModel
import com.diegocunha.pokedex.datasource.db.dao.PokemonListEntryDao
import com.diegocunha.pokedex.datasource.sync.PokemonSyncManager
import com.diegocunha.pokedex.datasource.sync.SyncState
import com.diegocunha.pokedex.feature.pokemon.domain.mapper.toDomain
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModel(
    private val syncManager: PokemonSyncManager,
    private val listEntryDao: PokemonListEntryDao,
    private val dispatchers: DispatchersProvider
) : BaseViewModel<PokemonListState, PokemonListIntent>(PokemonListState.Loading) {

    private val _effects = Channel<PokemonListEffect>(Channel.UNLIMITED)
    val effects: Flow<PokemonListEffect> = _effects.receiveAsFlow()

    val pagingFlow: Flow<PagingData<PokemonEntry>> = syncManager.syncState
        .filter { it is SyncState.Success }
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 30)
            ) {
                listEntryDao.pagingSource()
            }.flow
        }
        .map { pagingData -> pagingData.map { it.toDomain() } }
        .cachedIn(viewModelScope)

    init {
        observeSyncState()
        syncManager.sync()
    }

    private fun observeSyncState() {
        viewModelScope.launch {
            syncManager.syncState.collect { syncState ->
                when (syncState) {
                    is SyncState.Idle,
                    is SyncState.Loading -> updateState { PokemonListState.Loading }
                    is SyncState.Success -> updateState { PokemonListState.Success }
                    is SyncState.Error -> updateState { PokemonListState.Error(syncState.exception) }
                }
            }
        }
    }

    override fun processIntent(intent: PokemonListIntent) {
        when (intent) {
            is PokemonListIntent.SelectPokemon -> {
                viewModelScope.launch {
                    _effects.send(PokemonListEffect.NavigateToDetail(intent.id))
                }
            }
            is PokemonListIntent.Retry -> syncManager.sync()
        }
    }
}
