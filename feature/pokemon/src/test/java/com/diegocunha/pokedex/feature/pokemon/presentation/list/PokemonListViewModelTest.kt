package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.turbine.test
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.PokedexDatabase
import com.diegocunha.pokedex.datasource.db.dao.PokemonListEntryDao
import com.diegocunha.pokedex.datasource.db.dao.RemoteKeyDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import io.mockk.coEvery
import io.mockk.every
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

private class FakeEmptyPagingSource : PagingSource<Int, PokemonListEntryEntity>() {
    override fun getRefreshKey(state: PagingState<Int, PokemonListEntryEntity>) = null
    override suspend fun load(params: LoadParams<Int>) = LoadResult.Page<Int, PokemonListEntryEntity>(
        data = emptyList(),
        prevKey = null,
        nextKey = null
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : DispatchersProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
    }

    private val apiService: PokemonApiService = mockk()
    private val mockDatabase = mockk<PokedexDatabase>(relaxed = true)
    private val mockListEntryDao = mockk<PokemonListEntryDao>(relaxed = true)
    private val mockRemoteKeyDao = mockk<RemoteKeyDao>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { mockDatabase.pokemonListEntryDao() } returns mockListEntryDao
        every { mockDatabase.remoteKeyDao() } returns mockRemoteKeyDao
        every { mockListEntryDao.pagingSource() } returns FakeEmptyPagingSource()
        // Return a fresh timestamp so the mediator skips the initial refresh
        coEvery { mockRemoteKeyDao.oldestCreatedAt() } returns System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `SelectPokemon intent emits NavigateToDetail effect with the provided id`() = runTest {
        val viewModel = PokemonListViewModel(apiService, mockDatabase, testDispatchers)

        viewModel.effects.test {
            viewModel.sendIntent(PokemonListIntent.SelectPokemon(id = "1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertEquals(PokemonListEffect.NavigateToDetail("1"), effect)
        }
    }

    @Test
    fun `pagingFlow collects without error`() = runTest {
        val viewModel = PokemonListViewModel(apiService, mockDatabase, testDispatchers)

        viewModel.pagingFlow.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
