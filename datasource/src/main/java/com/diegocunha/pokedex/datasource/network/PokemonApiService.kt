package com.diegocunha.pokedex.datasource.network

import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.PokemonListResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.model.PokemonSpeciesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): PokemonResponse

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(@Path("id") id: Int): EvolutionChainResponse

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(@Path("id") id: Int): PokemonSpeciesResponse
}
