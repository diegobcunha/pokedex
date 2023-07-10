package com.br.diegocunha.pokedex.viewmodel

import app.cash.turbine.test
import com.br.diegocunha.pokedex.datasource.fixture.pokemon
import com.br.diegocunha.pokedex.datasource.fixture.pokemonUi
import com.br.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.br.diegocunha.pokedex.helper.BaseUnitTest
import com.br.diegocunha.pokedex.templates.GetStatus
import com.br.diegocunha.pokedex.ui.detail.PokemonDetailViewModel
import com.br.diegocunha.pokedex.ui.model.toPokemonUI
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PokemonDetailViewModelTest : BaseUnitTest() {

    private lateinit var viewModel: PokemonDetailViewModel
    private val repository = mockk<PokemonRepository>()

    @Before
    fun setup() {
        viewModel = PokemonDetailViewModel(testDispatchersProvider, repository, 1)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `WHEN fetch mode is called THEN should call repository`() = runBlockingTest {
        coEvery { repository.getPokemonDetail(any()) } returns Result.success(pokemon)

        viewModel.stateFlow.test {
            val item = awaitItem()
            assertTrue(item.currentStatus() == GetStatus.SUCCESS)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN api call returns error THEN should call error`() = runBlockingTest {
        coEvery { repository.getPokemonDetail(any()) } returns Result.failure(Exception())

        viewModel.stateFlow.test {
            val item = awaitItem()
            assertTrue(item.currentStatus() == GetStatus.FAILURE)
            assertTrue(item.failure.throwable is Exception)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `WHEN api call returns success THEN should map to pokemon ui`() = runBlockingTest {
        coEvery { repository.getPokemonDetail(any()) } returns Result.success(pokemon)
        mockkStatic("com.br.diegocunha.pokedex.ui.model.PokemonUiModelKt")
        every { pokemon.toPokemonUI() } returns pokemonUi

        viewModel.stateFlow.test {
            awaitItem()
            verify { pokemon.toPokemonUI() }
            cancelAndIgnoreRemainingEvents()
        }
    }
}