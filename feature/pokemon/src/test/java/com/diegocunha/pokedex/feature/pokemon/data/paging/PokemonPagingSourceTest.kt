package com.diegocunha.pokedex.feature.pokemon.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.model.PokemonAbilitySlot
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonListResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.model.PokemonSprites
import com.diegocunha.pokedex.datasource.model.PokemonStatSlot
import com.diegocunha.pokedex.datasource.model.PokemonType
import com.diegocunha.pokedex.datasource.model.PokemonTypeSlot
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonPagingSourceTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatchers = object : DispatchersProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
    }

    private val mockApiService = mockk<PokemonApiService>()
    private val config = PagingConfig(pageSize = 20, initialLoadSize = 20)

    private fun entryResponses(count: Int = 3) = (1..count).map {
        PokemonEntryResponse("pokemon-$it", "https://pokeapi.co/api/v2/pokemon/$it/")
    }

    private fun domainEntries(count: Int = 3) = (1..count).map {
        PokemonEntry(id = "$it", name = "pokemon-$it")
    }

    private fun makePokemonResponse(
        id: Int,
        imageUrl: String? = "https://example.com/$id.png",
        types: List<Pair<Int, String>> = listOf(1 to "grass")
    ) = PokemonResponse(
        id = id,
        name = "pokemon-$id",
        height = 1,
        weight = 1,
        types = types.map { (slot, name) -> PokemonTypeSlot(slot, PokemonType(name, "")) },
        stats = emptyList<PokemonStatSlot>(),
        sprites = PokemonSprites(frontDefault = imageUrl, frontShiny = null),
        abilities = emptyList<PokemonAbilitySlot>()
    )

    private fun pagingSource() = PokemonPagingSource(mockApiService, testDispatchers)

    @Test
    fun `first page load has null prevKey`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, entryResponses())
        coEvery { mockApiService.getPokemonDetail(any()) } throws RuntimeException("fallback")

        val pager = TestPager(config, pagingSource())
        val result = pager.refresh() as LoadResult.Page

        assertNull(result.prevKey)
        assertEquals(domainEntries(), result.data)
    }

    @Test
    fun `first page has nextKey equal to page size when next is not null`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(100, "next_url", null, entryResponses())
        coEvery { mockApiService.getPokemonDetail(any()) } throws RuntimeException("fallback")

        val pager = TestPager(config, pagingSource())
        val result = pager.refresh() as LoadResult.Page

        assertEquals(20, result.nextKey)
    }

    @Test
    fun `nextKey is null when response next is null`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(3, null, null, entryResponses())
        coEvery { mockApiService.getPokemonDetail(any()) } throws RuntimeException("fallback")

        val pager = TestPager(config, pagingSource())
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
        coEvery { mockApiService.getPokemonDetail(any()) } throws RuntimeException("fallback")

        val pager = TestPager(config, pagingSource())
        pager.refresh()
        val result = pager.append() as LoadResult.Page

        assertEquals(0, result.prevKey)
        assertEquals(secondDomain, result.data)
    }

    @Test
    fun `error case returns LoadResult Error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockApiService.getPokemonList(any(), any()) } throws exception

        val pager = TestPager(config, pagingSource())
        val result = pager.refresh()

        assertTrue(result is LoadResult.Error)
    }

    @Test
    fun `enriched page contains imageUrl and types from detail response`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(1, null, null, entryResponses(1))
        coEvery { mockApiService.getPokemonDetail(1) } returns
            makePokemonResponse(id = 1, imageUrl = "https://example.com/1.png", types = listOf(1 to "fire"))

        val pager = TestPager(config, pagingSource())
        val result = pager.refresh() as LoadResult.Page

        assertEquals("https://example.com/1.png", result.data[0].imageUrl)
        assertEquals(listOf("fire"), result.data[0].types)
    }

    @Test
    fun `types are sorted by slot so primary type is first`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(1, null, null, entryResponses(1))
        coEvery { mockApiService.getPokemonDetail(1) } returns
            makePokemonResponse(id = 1, types = listOf(2 to "poison", 1 to "grass"))

        val pager = TestPager(config, pagingSource())
        val result = pager.refresh() as LoadResult.Page

        assertEquals(listOf("grass", "poison"), result.data[0].types)
    }

    @Test
    fun `failed detail call falls back to entry with null imageUrl and empty types`() = runTest {
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(1, null, null, entryResponses(1))
        coEvery { mockApiService.getPokemonDetail(any()) } throws RuntimeException("Network error")

        val pager = TestPager(config, pagingSource())
        val result = pager.refresh() as LoadResult.Page

        assertNull(result.data[0].imageUrl)
        assertTrue(result.data[0].types.isEmpty())
    }

    @Test
    fun `all detail calls are made for every entry in the page`() = runTest {
        val entries = entryResponses(3)
        coEvery { mockApiService.getPokemonList(any(), 0) } returns
            PokemonListResponse(3, null, null, entries)
        coEvery { mockApiService.getPokemonDetail(1) } returns
            makePokemonResponse(id = 1, imageUrl = "https://example.com/1.png", types = listOf(1 to "grass"))
        coEvery { mockApiService.getPokemonDetail(2) } returns
            makePokemonResponse(id = 2, imageUrl = "https://example.com/2.png", types = listOf(1 to "fire"))
        coEvery { mockApiService.getPokemonDetail(3) } returns
            makePokemonResponse(id = 3, imageUrl = "https://example.com/3.png", types = listOf(1 to "water"))

        val pager = TestPager(config, pagingSource())
        val result = pager.refresh() as LoadResult.Page

        assertEquals(3, result.data.size)
        assertEquals("https://example.com/1.png", result.data[0].imageUrl)
        assertEquals("https://example.com/2.png", result.data[1].imageUrl)
        assertEquals("https://example.com/3.png", result.data[2].imageUrl)
    }
}
