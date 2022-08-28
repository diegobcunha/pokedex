package com.br.diegocunha.pokedex.datasource.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import com.br.diegocunha.pokedex.datasource.source.PokemonSource
import kotlinx.coroutines.flow.Flow

class PokemonRepositoryImpl(
    private val api: PokeDexAPI,
    private val dispatchersProvider: DispatchersProvider
) : PokemonRepository {

    override fun getPokeDex(
        pageSize: Int,
        filter: String?
    ): Flow<PagingData<SinglePokemonResult>> {
        return Pager(
            PagingConfig(
                pageSize = pageSize,
                prefetchDistance = 2,
                initialLoadSize = pageSize
            )
        ) { PokemonSource(api, filter, dispatchersProvider) }.flow
    }

    override suspend fun getPokemonDetail(id: Int): Result<SinglePokemonResult> {
        return api.getPokemonById(id)
    }
}