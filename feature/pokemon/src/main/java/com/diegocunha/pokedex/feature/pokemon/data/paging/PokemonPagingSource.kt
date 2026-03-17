package com.diegocunha.pokedex.feature.pokemon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.feature.pokemon.domain.mapper.toDomain
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

class PokemonPagingSource(
    private val apiService: PokemonApiService,
    private val dispatchers: DispatchersProvider
) : PagingSource<Int, PokemonEntry>() {

    private val PokemonEntryResponse.numericId: Int
        get() = url.trimEnd('/').substringAfterLast('/').toInt()

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

            val semaphore = Semaphore(params.loadSize)
            val enriched = withContext(dispatchers.io()) {
                response.results.map { entry ->
                    async {
                        semaphore.withPermit {
                            val detail = runCatching {
                                apiService.getPokemonDetail(entry.numericId)
                            }.getOrNull()
                            if (detail != null) entry.toDomain(detail) else entry.toDomain()
                        }
                    }
                }.awaitAll()
            }

            LoadResult.Page(
                data = enriched,
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (response.next == null) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
