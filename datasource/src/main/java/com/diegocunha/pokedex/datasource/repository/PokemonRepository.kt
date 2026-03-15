package com.diegocunha.pokedex.datasource.repository

import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse

interface PokemonRepository {
    suspend fun getPokemonDetail(id: Int): Resource<PokemonResponse>
    suspend fun getEvolutionChain(id: Int): Resource<EvolutionChainResponse>
}
