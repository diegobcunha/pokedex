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
import com.diegocunha.pokedex.datasource.db.dao.PokemonSearchQueryBuilder
import com.diegocunha.pokedex.datasource.sync.PokemonSyncManager
import com.diegocunha.pokedex.datasource.sync.SyncState
import com.diegocunha.pokedex.feature.pokemon.domain.mapper.toDomain
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import com.diegocunha.pokedex.feature.pokemon.domain.model.SearchFilter
import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModel(
    private val syncManager: PokemonSyncManager,
    private val listEntryDao: PokemonListEntryDao,
    private val dispatchers: DispatchersProvider
) : BaseViewModel<PokemonListState, PokemonListIntent>(PokemonListState.Loading) {

    private val _effects = Channel<PokemonListEffect>(Channel.UNLIMITED)
    val effects: Flow<PokemonListEffect> = _effects.receiveAsFlow()

    private val _rawQuery = MutableStateFlow("")
    private val _selectedTypes = MutableStateFlow<Set<PokemonType>>(emptySet())

    // Exposed to UI — raw (non-debounced) query for responsive TextField,
    // eagerly shared so .value is always current.
    val searchFilter: StateFlow<SearchFilter> = combine(_rawQuery, _selectedTypes) { query, types ->
        SearchFilter(query = query, selectedTypes = types)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SearchFilter())

    // Blank query emits immediately; non-blank query waits 2s after the user stops typing.
    // This ensures type-filter changes are never blocked by the name debounce.
    private val debouncedQuery: Flow<String> = _rawQuery.flatMapLatest { query ->
        if (query.isBlank()) flowOf(query)
        else flow {
            delay(2_000)
            emit(query)
        }
    }

    private val effectiveFilter: Flow<SearchFilter> = combine(
        debouncedQuery,
        _selectedTypes
    ) { query, types -> SearchFilter(query = query, selectedTypes = types) }

    val pagingFlow: Flow<PagingData<PokemonEntry>> = syncManager.syncState
        .filter { it is SyncState.Success }
        .take(1)
        .flatMapLatest { effectiveFilter }
        .flatMapLatest { filter ->
            Pager(config = PagingConfig(pageSize = 30)) {
                listEntryDao.pagingSourceFiltered(
                    PokemonSearchQueryBuilder.build(
                        nameQuery = filter.query,
                        typeNames = filter.selectedTypes.map { it.name.lowercase() }
                    )
                )
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
            is PokemonListIntent.UpdateQuery -> _rawQuery.value = intent.query
            is PokemonListIntent.ToggleTypeFilter -> _selectedTypes.update { types ->
                if (intent.type in types) types - intent.type else types + intent.type
            }
            is PokemonListIntent.ClearFilters -> {
                _rawQuery.value = ""
                _selectedTypes.value = emptySet()
            }
        }
    }
}
