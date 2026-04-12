package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import app.cash.turbine.test
import com.diegocunha.pokedex.core.Resource
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
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.diegocunha.pokedex.feature.pokemon.domain.mapper.toDomain
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: PokemonRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init emits Success when both detail and evolution return success`() = runTest {
        every { repository.getPokemonDetail(1) } returns flowOf(Resource.Success(fakePokemon()))
        every { repository.getEvolutionData(1) } returns flowOf(Resource.Success(fakeChain()))
        val viewModel = PokemonDetailViewModel("1", repository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is PokemonDetailState.Success)
        assertEquals(fakePokemon().toDomain(), (state as PokemonDetailState.Success).pokemon)
    }

    @Test
    fun `init emits Error when detail returns error`() = runTest {
        every { repository.getPokemonDetail(1) } returns flowOf(Resource.Error(RuntimeException("error")))
        every { repository.getEvolutionData(1) } returns flowOf(Resource.Success(fakeChain()))
        val viewModel = PokemonDetailViewModel("1", repository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is PokemonDetailState.Error)
    }

    @Test
    fun `init emits Error when evolution returns error`() = runTest {
        every { repository.getPokemonDetail(1) } returns flowOf(Resource.Success(fakePokemon()))
        every { repository.getEvolutionData(1) } returns flowOf(Resource.Error(RuntimeException("error")))
        val viewModel = PokemonDetailViewModel("1", repository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is PokemonDetailState.Error)
    }

    @Test
    fun `Retry intent re-fetches and updates state to Success`() = runTest {
        every { repository.getPokemonDetail(1) } returnsMany listOf(
            flowOf(Resource.Error(RuntimeException("first failure"))),
            flowOf(Resource.Success(fakePokemon()))
        )
        every { repository.getEvolutionData(1) } returnsMany listOf(
            flowOf(Resource.Error(RuntimeException("first failure"))),
            flowOf(Resource.Success(fakeChain()))
        )
        val viewModel = PokemonDetailViewModel("1", repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value is PokemonDetailState.Error)

        viewModel.sendIntent(PokemonDetailIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is PokemonDetailState.Success)
    }

    @Test
    fun `NavigateToPokemon intent emits NavigateToPokemon effect`() = runTest {
        every { repository.getPokemonDetail(1) } returns flowOf(Resource.Success(fakePokemon()))
        every { repository.getEvolutionData(1) } returns flowOf(Resource.Success(fakeChain()))
        val viewModel = PokemonDetailViewModel("1", repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.sendIntent(PokemonDetailIntent.NavigateToPokemon("2"))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertEquals(PokemonDetailEffect.NavigateToPokemon("2"), effect)
        }
    }

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

    private fun fakeChain() = EvolutionChainResponse(
        id = 1,
        chain = ChainLink(
            species = NamedResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/"),
            evolvesTo = emptyList()
        )
    )
}
