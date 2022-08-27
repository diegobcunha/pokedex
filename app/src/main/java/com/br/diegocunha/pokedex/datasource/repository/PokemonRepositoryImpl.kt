package com.br.diegocunha.pokedex.datasource.repository

import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.api.model.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class PokemonRepositoryImpl(
    private val api: PokeDexAPI,
    private val dispatchersProvider: DispatchersProvider
) : PokemonRepository {

    override suspend fun getPokeDex(limit: Int, offset: Int): PokeDex {
        val request = api.getPokemonList(limit, offset)
        val pokemons = request.results
        val pokemonInfo = getPokemonListInfo(pokemons.map { it.name })
        return request.transform(pokemonInfo.transform(pokemons))
    }

    override suspend fun getPokemonDetail(id: Int): Result<SinglePokemonResult> {
        return Result.success(
            SinglePokemonResult(
                1,
                Sprites("", "", "", ""),
                emptyList(),
                0,
                0,
                emptyList()
            )
        )
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

    private fun PokemonResponse.transform(pokemons: List<Pokemon>): PokeDex {
        return PokeDex(
            count = count,
            next = next,
            previous = previous,
            pokemons = pokemons
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
}