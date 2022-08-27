package com.br.diegocunha.pokedex.datasource.repository

import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.fixture.pokemonEmptyResponse
import com.br.diegocunha.pokedex.datasource.fixture.pokemonResponse
import com.br.diegocunha.pokedex.datasource.fixture.pokemonResult
import com.br.diegocunha.pokedex.datasource.fixture.singlePokemonResult
import com.br.diegocunha.pokedex.testers.BaseUnitTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class PokemonRepositoryImplTest : BaseUnitTest() {

    private val api: PokeDexAPI = mockk()
    private lateinit var repositoryImpl: PokemonRepositoryImpl

    @Before
    fun setup() {
        clearAllMocks()
        repositoryImpl = PokemonRepositoryImpl(api, testDispatchersProvider)
    }

    @Test
    fun `WHEN api is called to get pokemons list THEN should call api resource`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonEmptyResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            repositoryImpl.getPokeDex(10, 1)

            coVerify(exactly = 1) { api.getPokemonList(10, 1) }
        }

    @Test
    fun `WHEN api contains more then zero results THEN should call detail response correctly`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            repositoryImpl.getPokeDex(10, 1)

            coVerify(exactly = 1) { api.getPokemonList(10, 1) }
            coVerify(exactly = pokemonResponse.results.size) { api.getPokemon(any()) }
        }


    @Test
    fun `WHEN api returns valid response THEN should parse response to be consumed`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            val response = repositoryImpl.getPokeDex(10, 1)
            response.pokemons.forEach { pokemon ->
                assertEquals(pokemonResult().name, pokemon.name)

                assertEquals(singlePokemonResult.height, pokemon.height)
                assertEquals(singlePokemonResult.id, pokemon.id)
                assertEquals(singlePokemonResult.sprites, pokemon.sprites)
                assertEquals(singlePokemonResult.stats, pokemon.stats)
                assertEquals(singlePokemonResult.types.size, pokemon.types.size)
            }
        }

    @Test(expected = Exception::class)
    fun `WHEN api returns error THEN should throw exception`() = runBlockingTest {
        coEvery { api.getPokemonList(any(), any()) } throws Exception()
        coEvery { api.getPokemon(any()) } throws Exception()

        repositoryImpl.getPokeDex(10, 1)
        coVerify(inverse = true) { api.getPokemon(any()) }
    }
}