package com.br.diegocunha.pokedex.ui.home

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.api.model.Pokemon
import com.br.diegocunha.pokedex.datasource.source.PokemonSource
import com.br.diegocunha.pokedex.templates.PaginationViewModel
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    dispatchersProvider: DispatchersProvider,
    private val source: PokemonSource
) : PaginationViewModel<Pokemon>(dispatchersProvider, 10) {

    override fun fetchContent(initialPageSize: Int): Flow<PagingData<Pokemon>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                initialLoadSize = 10
            )
        ) { source }.flow
    }
}