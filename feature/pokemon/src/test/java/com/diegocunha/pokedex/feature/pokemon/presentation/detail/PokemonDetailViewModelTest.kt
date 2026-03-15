package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import app.cash.turbine.test
import com.diegocunha.pokedex.core.Resource
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
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    fun `init emits Loading then Success when repository returns success`() = runTest {
        coEvery { repository.getPokemonDetail(1) } returns Resource.Success(fakePokemon())
        val viewModel = PokemonDetailViewModel("1", repository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is PokemonDetailState.Success)
        assertEquals(fakePokemon().toDomain(), (state as PokemonDetailState.Success).pokemon)
    }

    @Test
    fun `init emits Error when repository returns error`() = runTest {
        coEvery { repository.getPokemonDetail(1) } returns Resource.Error(RuntimeException("error"))
        val viewModel = PokemonDetailViewModel("1", repository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is PokemonDetailState.Error)
    }

    @Test
    fun `Retry intent re-fetches and updates state to Success`() = runTest {
        coEvery { repository.getPokemonDetail(1) } returnsMany listOf(
            Resource.Error(RuntimeException("first failure")),
            Resource.Success(fakePokemon())
        )
        val viewModel = PokemonDetailViewModel("1", repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value is PokemonDetailState.Error)

        viewModel.sendIntent(PokemonDetailIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is PokemonDetailState.Success)
    }

    @Test
    fun `NavigateToEvolution intent emits NavigateToEvolution effect when state is Success`() = runTest {
        coEvery { repository.getPokemonDetail(1) } returns Resource.Success(fakePokemon())
        val viewModel = PokemonDetailViewModel("1", repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.sendIntent(PokemonDetailIntent.NavigateToEvolution)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertEquals(PokemonDetailEffect.NavigateToEvolution("1"), effect)
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
}
