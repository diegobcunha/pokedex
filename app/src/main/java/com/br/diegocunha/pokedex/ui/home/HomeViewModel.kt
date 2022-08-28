package com.br.diegocunha.pokedex.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.br.diegocunha.pokedex.datasource.repository.PokemonUI
import com.br.diegocunha.pokedex.datasource.repository.toPokemonUI
import com.br.diegocunha.pokedex.extensions.transformPagingData
import com.br.diegocunha.pokedex.templates.PaginationViewModel
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    dispatchersProvider: DispatchersProvider,
    private val repository: PokemonRepository
) : PaginationViewModel<PokemonUI>(dispatchersProvider, 1) {

    private var _currentSearch = mutableStateOf("")
    val currentSearch = _currentSearch.value

    fun updateCurrentSearch(text: String?) {
        _currentSearch.value = text ?: ""

    }

    override fun fetchContent(initialPageSize: Int): Flow<PagingData<PokemonUI>> {
        return repository.getPokeDex(initialPageSize, _currentSearch.value).transformPagingData {
            it.toPokemonUI()
        }
    }
}