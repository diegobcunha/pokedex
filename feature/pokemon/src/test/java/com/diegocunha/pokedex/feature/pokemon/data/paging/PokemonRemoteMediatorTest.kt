package com.diegocunha.pokedex.feature.pokemon.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.PokedexDatabase
import com.diegocunha.pokedex.datasource.db.dao.RemoteKeyDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.db.entity.RemoteKeyEntity
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalPagingApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PokemonRemoteMediatorTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatchers = object : DispatchersProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
    }

    private val mockApiService = mockk<PokemonApiService>()
    private val mockDatabase = mockk<PokedexDatabase>(relaxed = true)
    private val mockRemoteKeyDao = mockk<RemoteKeyDao>(relaxed = true)

    init {
        every { mockDatabase.remoteKeyDao() } returns mockRemoteKeyDao
    }

    private fun mediator() = PokemonRemoteMediator(mockApiService, mockDatabase, testDispatchers)

    private fun emptyPagingState() = PagingState<Int, PokemonListEntryEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0
    )

    private fun pagingStateWithLastItem(id: String) = PagingState(
        pages = listOf(
            androidx.paging.PagingSource.LoadResult.Page(
                data = listOf(
                    PokemonListEntryEntity(id = id, name = "pokemon-$id", imageUrl = null, types = "[]")
                ),
                prevKey = null,
                nextKey = 20
            )
        ),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0
    )

    @Test
    fun `initialize returns SKIP_INITIAL_REFRESH when cache is fresh`() = runTest {
        val freshTimestamp = System.currentTimeMillis() - 1000L
        coEvery { mockRemoteKeyDao.oldestCreatedAt() } returns freshTimestamp

        val result = mediator().initialize()

        assertEquals(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH, result)
    }

    @Test
    fun `initialize returns LAUNCH_INITIAL_REFRESH when cache is stale`() = runTest {
        val staleTimestamp = System.currentTimeMillis() - 25 * 60 * 60 * 1000L
        coEvery { mockRemoteKeyDao.oldestCreatedAt() } returns staleTimestamp

        val result = mediator().initialize()

        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
    }

    @Test
    fun `initialize returns LAUNCH_INITIAL_REFRESH when cache is empty`() = runTest {
        coEvery { mockRemoteKeyDao.oldestCreatedAt() } returns null

        val result = mediator().initialize()

        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
    }

    @Test
    fun `load APPEND returns end of pagination when no next key`() = runTest {
        coEvery { mockRemoteKeyDao.remoteKeyFor("1") } returns RemoteKeyEntity(
            pokemonId = "1",
            prevKey = null,
            nextKey = null
        )

        val result = mediator().load(LoadType.APPEND, pagingStateWithLastItem("1"))

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(true, (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load APPEND network error returns MediatorResult Error`() = runTest {
        coEvery { mockRemoteKeyDao.remoteKeyFor("1") } returns RemoteKeyEntity(
            pokemonId = "1",
            prevKey = 0,
            nextKey = 20
        )
        coEvery { mockApiService.getPokemonList(any(), 20) } throws RuntimeException("Network error")

        val result = mediator().load(LoadType.APPEND, pagingStateWithLastItem("1"))

        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun `load PREPEND always returns end of pagination`() = runTest {
        val result = mediator().load(LoadType.PREPEND, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(true, (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}
