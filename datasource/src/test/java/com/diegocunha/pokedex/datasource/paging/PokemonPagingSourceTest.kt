package com.diegocunha.pokedex.datasource.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonListResponse
import com.diegocunha.pokedex.datasource.network.PokemonApiService
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

    private fun entries(count: Int = 3) = (1..count).map {
        PokemonEntryResponse("pokemon-$it", "https://pokeapi.co/api/v2/pokemon/$it/")
    }

    @Test
    fun `first page load has null prevKey`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, entries())

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        val result = pager.refresh() as LoadResult.Page

        assertNull(result.prevKey)
        assertEquals(entries(), result.data)
    }

    @Test
    fun `first page has nextKey equal to page size when next is not null`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, entries())

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        val result = pager.refresh() as LoadResult.Page

        assertEquals(20, result.nextKey)
    }

    @Test
    fun `nextKey is null when response next is null`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(3, null, null, entries())

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        val result = pager.refresh() as LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `subsequent page has non-null prevKey`() = runTest {
        val firstEntries = entries()
        val secondEntries = (4..6).map {
            PokemonEntryResponse("pokemon-$it", "https://pokeapi.co/api/v2/pokemon/$it/")
        }
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, firstEntries)
        coEvery { mockApiService.getPokemonList(any(), 20) } returns
            PokemonListResponse(100, "next_url", "prev_url", secondEntries)

        val pager = TestPager(config, PokemonPagingSource(mockApiService))
        pager.refresh()
        val result = pager.append() as LoadResult.Page

        assertEquals(0, result.prevKey)
        assertEquals(secondEntries, result.data)
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
