package com.br.diegocunha.pokedex.datasource.repository

import app.cash.turbine.test
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.fixture.pokemonEmptyResponse
import com.br.diegocunha.pokedex.datasource.fixture.pokemonResponse
import com.br.diegocunha.pokedex.datasource.fixture.singlePokemonResult
import com.br.diegocunha.pokedex.helper.BaseUnitTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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
    fun `WHEN pokemons are required with success THEN should return valid data`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonEmptyResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            repositoryImpl.getPokeDex(10, null).test {
                val item = awaitItem()
                assertNotNull(item)
            }
        }


    @Test
    fun `WHEN api returns valid response THEN should parse response to be consumed`() =
        runBlockingTest {
            coEvery { api.getPokemonList(any(), any()) } returns pokemonResponse
            coEvery { api.getPokemon(any()) } returns singlePokemonResult

            repositoryImpl.getPokeDex(10, "").test {
                val item = awaitItem()
            }
        }
}