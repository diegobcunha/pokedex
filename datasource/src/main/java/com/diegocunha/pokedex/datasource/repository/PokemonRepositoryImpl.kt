package com.diegocunha.pokedex.datasource.repository

import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.datasource.network.safeApiCall

class PokemonRepositoryImpl(
    private val apiService: PokemonApiService,
    private val dispatchersProvider: DispatchersProvider,
) : PokemonRepository {

    override suspend fun getPokemonDetail(id: Int): Resource<PokemonResponse> =
        safeApiCall(dispatchersProvider) { apiService.getPokemonDetail(id) }

    override suspend fun getEvolutionChain(id: Int): Resource<EvolutionChainResponse> =
        safeApiCall(dispatchersProvider) { apiService.getEvolutionChain(id) }
}
