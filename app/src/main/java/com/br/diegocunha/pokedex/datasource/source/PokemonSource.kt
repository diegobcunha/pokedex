package com.br.diegocunha.pokedex.datasource.source

import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.api.model.Pokemon
import com.br.diegocunha.pokedex.datasource.api.model.PokemonResult
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
    BasePaginationSource<Pokemon>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        return try {
            val nextPage = params.key ?: INITIAL_OFFSET
            val response = api.getPokemonList(limit = LIMIT, offset = nextPage)
            val results = response.results
            val pokemonInfo = getPokemonListInfo(results.map { it.name })
            val pokemons = pokemonInfo.transform(results)

            LoadResult.Page(
                data = pokemons,
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

    private fun List<SinglePokemonResult>.transform(basePokemon: List<PokemonResult>) =
        mapIndexed { index, singlePokemonResult ->
            (basePokemon[index] to singlePokemonResult).toPokemon()
        }

    private fun Pair<PokemonResult, SinglePokemonResult>.toPokemon(): Pokemon {
        return Pokemon(
            id = second.id,
            name = first.name,
            sprites = second.sprites,
            height = second.height,
            weight = second.weight,
            stats = second.stats,
            types = second.types.map { it.type }
        )
    }

    companion object {
        private const val INITIAL_OFFSET = 0
        private const val LIMIT = 10
    }
}