package com.diegocunha.pokedex.datasource.repository

import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonDetail(id: Int): Flow<Resource<PokemonResponse>>
    suspend fun getEvolutionChain(id: Int): Resource<EvolutionChainResponse>
}
