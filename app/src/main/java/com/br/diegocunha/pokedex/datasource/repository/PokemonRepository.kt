package com.br.diegocunha.pokedex.datasource.repository

import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.api.model.PokemonResponse
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult

class PokemonRepository(private val api: PokeDexAPI) {

    suspend fun getPokeDex(limit: Int, offset: Int): Result<PokemonResponse> {
        return api.getPokemonList(limit, offset)
    }

    suspend fun getPokemonDetail(id: Int): Result<SinglePokemonResult> {
        return api.getPokemon(id)
    }
}