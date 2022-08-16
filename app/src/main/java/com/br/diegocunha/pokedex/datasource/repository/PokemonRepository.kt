package com.br.diegocunha.pokedex.datasource.repository

import com.br.diegocunha.pokedex.datasource.api.model.PokeDex
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult

interface PokemonRepository {

    suspend fun getPokeDex(limit: Int, offset: Int): PokeDex

    suspend fun getPokemonDetail(id: Int): Result<SinglePokemonResult>
}