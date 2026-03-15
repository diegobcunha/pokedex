package com.diegocunha.pokedex.datasource.repository

import androidx.paging.PagingData
import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemonList(): Flow<PagingData<PokemonEntryResponse>>
    suspend fun getPokemonDetail(id: Int): Resource<PokemonResponse>
    suspend fun getEvolutionChain(id: Int): Resource<EvolutionChainResponse>
}
