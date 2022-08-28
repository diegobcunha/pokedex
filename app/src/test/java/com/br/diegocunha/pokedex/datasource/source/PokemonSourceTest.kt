package com.br.diegocunha.pokedex.datasource.source

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Page
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.fixture.pokemon
import com.br.diegocunha.pokedex.datasource.fixture.pokemonEmptyResponse
import com.br.diegocunha.pokedex.datasource.fixture.pokemonResponse
import com.br.diegocunha.pokedex.datasource.fixture.singlePokemonResult
import com.br.diegocunha.pokedex.helper.BaseUnitTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PokemonSourceTest : BaseUnitTest() {

    private val api = mockk<PokeDexAPI>()
    private lateinit var pokemonSource: PokemonSource

    @Before
    fun setup() {
        clearAllMocks()
        pokemonSource = PokemonSource(api, null, testDispatchersProvider)
    }

    @Test
    fun `WHEN api is called to get pokemons list THEN should call api resource`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonEmptyResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            pokemonSource.load(
                Refresh(
                    key = null,
                    loadSize = 1,
                    placeholdersEnabled = true
                )
            )
            coVerify(exactly = 1) { api.getPokemonList(10, 0) }
        }

    @Test
    fun `WHEN main network call returns error THEN should return error page`() = runBlockingTest {
        coEvery { api.getPokemonList(any(), any()) } throws Exception()
        coEvery { api.getPokemon(any()) } throws Exception()

        assertTrue(
            pokemonSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            ) is PagingSource.LoadResult.Error
        )
    }

    @Test
    fun `WHEN api contains more then zero results THEN should call detail response correctly`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            pokemonSource.load(
                Refresh(
                    key = 1,
                    loadSize = 1,
                    placeholdersEnabled = true
                )
            )

            coVerify(exactly = 1) { api.getPokemonList(10, 1) }
            coVerify(exactly = pokemonResponse.results.size) { api.getPokemon(any()) }
        }

    @Test
    fun `WHEN api returns success data THEN should inform correctly`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            assertEquals(
                expected = Page(
                    data = listOf(pokemon),
                    prevKey = 0,
                    nextKey = 10
                ),
                actual = pokemonSource.load(
                    Refresh(
                        key = null,
                        loadSize = 2,
                        placeholdersEnabled = false
                    )
                )
            )
        }
}