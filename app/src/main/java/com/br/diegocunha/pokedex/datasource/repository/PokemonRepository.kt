package com.br.diegocunha.pokedex.datasource.repository

import androidx.paging.PagingData
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    fun getPokeDex(pageSize: Int, filter: String?): Flow<PagingData<SinglePokemonResult>>

    suspend fun getPokemonDetail(id: Int): Result<SinglePokemonResult>
}