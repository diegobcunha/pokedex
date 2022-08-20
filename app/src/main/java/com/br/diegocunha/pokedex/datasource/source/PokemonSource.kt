package com.br.diegocunha.pokedex.datasource.source

import com.br.diegocunha.pokedex.datasource.api.model.Pokemon
import com.br.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.br.diegocunha.pokedex.datasource.templates.BasePaginationSource

class PokemonSource(private val pokemonRepository: PokemonRepository) :
    BasePaginationSource<Pokemon>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        return try {
            val nextPage = params.key ?: INITIAL_OFFSET
            val response = pokemonRepository.getPokeDex(offset = nextPage, limit = LIMIT)
            LoadResult.Page(
                data = response.pokemons,
                prevKey = if (nextPage == INITIAL_OFFSET) null else nextPage - 1,
                nextKey = if (response.pokemons.isNotEmpty()) nextPage + 1 else null
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_OFFSET = 0
        private const val LIMIT = 10
    }
}