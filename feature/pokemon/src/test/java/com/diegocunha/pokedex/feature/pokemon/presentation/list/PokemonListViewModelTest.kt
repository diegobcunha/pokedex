package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.paging.PagingData
import app.cash.turbine.test
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import io.mockk.coEvery
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

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
    fun `SelectPokemon intent emits NavigateToDetail effect with extracted ID`() = runTest {
        coEvery { repository.getPokemonList() } returns flowOf(PagingData.empty())
        val viewModel = PokemonListViewModel(repository)

        viewModel.effects.test {
            viewModel.sendIntent(
                PokemonListIntent.SelectPokemon(
                    name = "bulbasaur",
                    url = "https://pokeapi.co/api/v2/pokemon/1/"
                )
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertEquals(PokemonListEffect.NavigateToDetail("1"), effect)
        }
    }

    @Test
    fun `SelectPokemon extracts correct ID from URL without trailing slash`() = runTest {
        coEvery { repository.getPokemonList() } returns flowOf(PagingData.empty())
        val viewModel = PokemonListViewModel(repository)

        viewModel.effects.test {
            viewModel.sendIntent(
                PokemonListIntent.SelectPokemon(
                    name = "charmander",
                    url = "https://pokeapi.co/api/v2/pokemon/4"
                )
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertEquals(PokemonListEffect.NavigateToDetail("4"), effect)
        }
    }

    @Test
    fun `pagingFlow collects without error`() = runTest {
        coEvery { repository.getPokemonList() } returns flowOf(PagingData.empty())
        val viewModel = PokemonListViewModel(repository)

        viewModel.pagingFlow.test {
            val item = awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
