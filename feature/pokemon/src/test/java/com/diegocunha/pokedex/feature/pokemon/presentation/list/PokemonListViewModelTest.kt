package com.diegocunha.pokedex.feature.pokemon.presentation.list

import app.cash.turbine.test
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.model.PokemonListResponse
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : DispatchersProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
    }
    private val apiService: PokemonApiService = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { apiService.getPokemonList(any(), any()) } returns
            PokemonListResponse(count = 0, next = null, previous = null, results = emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `SelectPokemon intent emits NavigateToDetail effect with the provided id`() = runTest {
        val viewModel = PokemonListViewModel(apiService, testDispatchers)

        viewModel.effects.test {
            viewModel.sendIntent(PokemonListIntent.SelectPokemon(id = "1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertEquals(PokemonListEffect.NavigateToDetail("1"), effect)
        }
    }

    @Test
    fun `pagingFlow collects without error`() = runTest {
        val viewModel = PokemonListViewModel(apiService, testDispatchers)

        viewModel.pagingFlow.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
