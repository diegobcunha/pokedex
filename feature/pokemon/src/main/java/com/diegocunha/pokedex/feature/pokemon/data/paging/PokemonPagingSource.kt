package com.diegocunha.pokedex.feature.pokemon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.feature.pokemon.domain.mapper.toDomain
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry

class PokemonPagingSource(
    private val apiService: PokemonApiService
) : PagingSource<Int, PokemonEntry>() {

    override fun getRefreshKey(state: PagingState<Int, PokemonEntry>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(state.config.pageSize)
                ?: closestPage?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonEntry> {
        return try {
            val offset = params.key ?: 0
            val response = apiService.getPokemonList(params.loadSize, offset)
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (response.next == null) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
