package com.diegocunha.pokedex.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.network.PokemonApiService

class PokemonPagingSource(
    private val apiService: PokemonApiService
) : PagingSource<Int, PokemonEntryResponse>() {

    override fun getRefreshKey(state: PagingState<Int, PokemonEntryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(state.config.pageSize)
                ?: closestPage?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonEntryResponse> {
        return try {
            val offset = params.key ?: 0
            val response = apiService.getPokemonList(params.loadSize, offset)
            LoadResult.Page(
                data = response.results,
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (response.next == null) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
