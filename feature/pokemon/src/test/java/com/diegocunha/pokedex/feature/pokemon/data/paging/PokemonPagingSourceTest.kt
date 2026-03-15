package com.diegocunha.pokedex.feature.pokemon.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonListResponse
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonPagingSourceTest {

    private val mockApiService = mockk<PokemonApiService>()
    private val config = PagingConfig(pageSize = 20, initialLoadSize = 20)

    private fun entryResponses(count: Int = 3) = (1..count).map {
        PokemonEntryResponse("pokemon-$it", "https://pokeapi.co/api/v2/pokemon/$it/")
    }

    private fun domainEntries(count: Int = 3) = (1..count).map {
        PokemonEntry(id = "$it", name = "pokemon-$it")
    }

    @Test
    fun `first page load has null prevKey`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, entryResponses())

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        val result = pager.refresh() as LoadResult.Page

        assertNull(result.prevKey)
        assertEquals(domainEntries(), result.data)
    }

    @Test
    fun `first page has nextKey equal to page size when next is not null`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, entryResponses())

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        val result = pager.refresh() as LoadResult.Page

        assertEquals(20, result.nextKey)
    }

    @Test
    fun `nextKey is null when response next is null`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(3, null, null, entryResponses())

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        val result = pager.refresh() as LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `subsequent page has non-null prevKey`() = runTest {
        val firstResponses = entryResponses()
        val secondResponses = (4..6).map {
            PokemonEntryResponse("pokemon-$it", "https://pokeapi.co/api/v2/pokemon/$it/")
        }
        val secondDomain = (4..6).map { PokemonEntry(id = "$it", name = "pokemon-$it") }

        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, firstResponses)
        coEvery { mockApiService.getPokemonList(any(), 20) } returns
            PokemonListResponse(100, "next_url", "prev_url", secondResponses)

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        pager.refresh()
        val result = pager.append() as LoadResult.Page

        assertEquals(0, result.prevKey)
        assertEquals(secondDomain, result.data)
    }

    @Test
    fun `error case returns LoadResult Error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockApiService.getPokemonList(any(), any()) } throws exception

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        val result = pager.refresh()

        assertTrue(result is LoadResult.Error)
    }
}
