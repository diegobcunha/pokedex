package com.diegocunha.pokedex.datasource.repository

import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.model.ChainLink
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.NamedResource
import com.diegocunha.pokedex.datasource.model.PokemonAbility
import com.diegocunha.pokedex.datasource.model.PokemonAbilitySlot
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.model.PokemonSprites
import com.diegocunha.pokedex.datasource.model.PokemonStat
import com.diegocunha.pokedex.datasource.model.PokemonStatSlot
import com.diegocunha.pokedex.datasource.model.PokemonType
import com.diegocunha.pokedex.datasource.model.PokemonTypeSlot
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

private val testDispatchers = object : DispatchersProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
}

class PokemonRepositoryImplTest {

    private val mockApiService = mockk<PokemonApiService>()
    private val repository = PokemonRepositoryImpl(mockApiService, testDispatchers)

    private fun fakePokemon(id: Int = 1) = PokemonResponse(
        id = id,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        types = listOf(PokemonTypeSlot(1, PokemonType("grass", "url"))),
        stats = listOf(PokemonStatSlot(45, 0, PokemonStat("hp", "url"))),
        sprites = PokemonSprites("front_url", null),
        abilities = listOf(PokemonAbilitySlot(PokemonAbility("overgrow", "url"), false, 1))
    )

    private fun fakeEvolutionChain(id: Int = 1) = EvolutionChainResponse(
        id = id,
        chain = ChainLink(
            species = NamedResource("bulbasaur", "url"),
            evolvesTo = emptyList()
        )
    )

    @Test
    fun `getPokemonDetail success returns Resource Success`() = runTest {
        val pokemon = fakePokemon()
        coEvery { mockApiService.getPokemonDetail(1) } returns pokemon

        val result = repository.getPokemonDetail(1)

        assertTrue(result is Resource.Success)
        assertEquals(pokemon, (result as Resource.Success).data)
    }

    @Test
    fun `getPokemonDetail throws returns Resource Error`() = runTest {
        val exception = RuntimeException("Not found")
        coEvery { mockApiService.getPokemonDetail(1) } throws exception

        val result = repository.getPokemonDetail(1)

        assertTrue(result is Resource.Error)
        assertSame(exception, (result as Resource.Error).exception)
    }

    @Test
    fun `getEvolutionChain success returns Resource Success`() = runTest {
        val chain = fakeEvolutionChain()
        coEvery { mockApiService.getEvolutionChain(1) } returns chain

        val result = repository.getEvolutionChain(1)

        assertTrue(result is Resource.Success)
        assertEquals(chain, (result as Resource.Success).data)
    }

    @Test
    fun `getEvolutionChain throws returns Resource Error`() = runTest {
        val exception = RuntimeException("Chain not found")
        coEvery { mockApiService.getEvolutionChain(1) } throws exception

        val result = repository.getEvolutionChain(1)

        assertTrue(result is Resource.Error)
        assertSame(exception, (result as Resource.Error).exception)
    }
}
