package com.br.diegocunha.pokedex.datasource.source

import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import com.br.diegocunha.pokedex.datasource.api.model.nextPage
import com.br.diegocunha.pokedex.datasource.api.model.prevPage
import com.br.diegocunha.pokedex.datasource.templates.BasePaginationSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class PokemonSource(
    private val api: PokeDexAPI,
    private val search: String?,
    private val dispatchersProvider: DispatchersProvider
) :
    BasePaginationSource<SinglePokemonResult>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SinglePokemonResult> {
        return try {
            val nextPage = params.key ?: INITIAL_OFFSET
            val response = api.getPokemonList(limit = LIMIT, offset = nextPage)
            val results = response.results
            val pokemonInfo = getPokemonListInfo(results.map { it.name })

            LoadResult.Page(
                data = pokemonInfo,
                prevKey = response.prevPage(),
                nextKey = response.nextPage()
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getPokemonListInfo(pokemonList: List<String>) =
        withContext(dispatchersProvider.io) {
            val items = mutableListOf<Deferred<SinglePokemonResult>>()
            pokemonList.forEach {
                items.add(async { api.getPokemon(it) })
            }

            awaitAll(
                *items.toTypedArray()
            )
        }

    companion object {
        private const val INITIAL_OFFSET = 0
        private const val LIMIT = 10
    }
}