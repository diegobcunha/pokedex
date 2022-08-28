package com.br.diegocunha.pokedex.datasource.repository

import android.os.Parcelable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.api.model.Pokemon
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import com.br.diegocunha.pokedex.datasource.api.model.Sprites
import com.br.diegocunha.pokedex.datasource.api.model.Stats
import com.br.diegocunha.pokedex.datasource.api.model.Type
import com.br.diegocunha.pokedex.datasource.source.PokemonSource
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

class PokemonRepositoryImpl(
    private val api: PokeDexAPI,
    private val dispatchersProvider: DispatchersProvider
) : PokemonRepository {

    override fun getPokeDex(
        pageSize: Int,
        filter: String?
    ): Flow<PagingData<Pokemon>> {
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

@Parcelize
data class PokemonUI(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<Stats>,
    val height: Int,
    val weight: Int,
    val types: List<Type>
) : Parcelable

fun Pokemon.toPokemonUI() = PokemonUI(
    id = id,
    name = name,
    sprites = sprites,
    height = height,
    weight = weight,
    stats = stats,
    types = types
)