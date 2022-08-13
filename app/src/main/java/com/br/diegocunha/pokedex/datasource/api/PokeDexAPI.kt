package com.br.diegocunha.pokedex.datasource.api

import com.br.diegocunha.pokedex.datasource.api.model.PokemonResponse
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeDexAPI {

    @GET("pokemon/")
    suspend fun getPokemonList(
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Result<PokemonResponse>

    @GET("pokemon/{id}/")
    suspend fun getPokemon(
        @Path("id") id: Int
    ): Result<SinglePokemonResult>
}