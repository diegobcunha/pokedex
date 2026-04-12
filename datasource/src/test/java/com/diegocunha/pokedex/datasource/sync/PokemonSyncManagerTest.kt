package com.diegocunha.pokedex.datasource.sync

import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.dao.PokemonListEntryDao
import com.diegocunha.pokedex.datasource.db.dao.SyncStateDao
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonListResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.model.PokemonSprites
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonSyncManagerTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatchers = object : DispatchersProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
    }

    // TransactionRunner passthrough — executes the block directly without Room
    private val transactionRunner = TransactionRunner { block -> block() }

    private val mockApiService = mockk<PokemonApiService>()
    private val mockListEntryDao = mockk<PokemonListEntryDao>(relaxed = true)
    private val mockSyncStateDao = mockk<SyncStateDao>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun CoroutineScope.syncManager() = PokemonSyncManager(
        apiService = mockApiService,
        listEntryDao = mockListEntryDao,
        syncStateDao = mockSyncStateDao,
        transactionRunner = transactionRunner,
        syncScope = this,
        dispatchers = testDispatchers,
        retryDelayMs = 0L
    )

    @Test
    fun `sync emits Success immediately when api count equals db count`() = runTest {
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } returns countResponse(100)
        coEvery { mockListEntryDao.count() } returns 100

        val manager = backgroundScope.syncManager()
        manager.sync()

        assertTrue(manager.syncState.value is SyncState.Success)
        coVerify(exactly = 0) { mockListEntryDao.insertAll(any()) }
    }

    @Test
    fun `sync emits Success without fetching when api count is lower than db count`() = runTest {
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } returns countResponse(50)
        coEvery { mockListEntryDao.count() } returns 100

        val manager = backgroundScope.syncManager()
        manager.sync()

        assertTrue(manager.syncState.value is SyncState.Success)
        coVerify(exactly = 0) { mockListEntryDao.insertAll(any()) }
    }

    @Test
    fun `sync fetches delta page and saves entries atomically`() = runTest {
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } returns countResponse(30)
        coEvery { mockListEntryDao.count() } returns 0
        coEvery { mockApiService.getPokemonList(limit = 30, offset = 0) } returns pageResponse(
            entries = (1..30).map { fakeEntry(it) }
        )
        (1..30).forEach { id ->
            coEvery { mockApiService.getPokemonDetail(id) } returns fakePokemonResponse(id)
        }

        val manager = backgroundScope.syncManager()
        manager.sync()

        assertTrue(manager.syncState.value is SyncState.Success)
        coVerify(exactly = 1) { mockListEntryDao.insertAll(any()) }
    }

    @Test
    fun `sync writes PENDING before fetching and COMPLETE after success`() = runTest {
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } returns countResponse(30)
        coEvery { mockListEntryDao.count() } returns 0
        coEvery { mockApiService.getPokemonList(limit = 30, offset = 0) } returns pageResponse(
            entries = (1..30).map { fakeEntry(it) }
        )
        (1..30).forEach { id ->
            coEvery { mockApiService.getPokemonDetail(id) } returns fakePokemonResponse(id)
        }

        val manager = backgroundScope.syncManager()
        manager.sync()

        coVerify(atLeast = 1) { mockSyncStateDao.upsert(match { it.status == "PENDING" }) }
        coVerify(atLeast = 1) { mockSyncStateDao.upsert(match { it.status == "COMPLETE" }) }
    }

    @Test
    fun `sync retries failed page up to 3 times and emits Error`() = runTest {
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } returns countResponse(30)
        coEvery { mockListEntryDao.count() } returns 0
        coEvery { mockApiService.getPokemonList(limit = 30, offset = 0) } throws RuntimeException("Network failure")

        val manager = backgroundScope.syncManager()
        manager.sync()

        assertTrue(manager.syncState.value is SyncState.Error)
        coVerify(exactly = 3) { mockApiService.getPokemonList(limit = 30, offset = 0) }
    }

    @Test
    fun `sync rolls back orphaned entries on failure`() = runTest {
        val previousCount = 10
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } returns countResponse(40)
        coEvery { mockListEntryDao.count() } returns previousCount
        coEvery { mockApiService.getPokemonList(limit = 30, offset = previousCount) } throws RuntimeException("Error")

        val manager = backgroundScope.syncManager()
        manager.sync()

        assertTrue(manager.syncState.value is SyncState.Error)
        coVerify(exactly = 1) { mockListEntryDao.deleteEntriesAfterOffset(previousCount) }
    }

    @Test
    fun `concurrent sync calls are deduplicated`() = runTest {
        // Latch keeps the first sync suspended so isSyncing remains true when subsequent calls arrive
        val latch = CompletableDeferred<Unit>()
        var callCount = 0
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } coAnswers {
            callCount++
            latch.await()
            countResponse(100)
        }
        coEvery { mockListEntryDao.count() } returns 100

        val manager = backgroundScope.syncManager()
        manager.sync()  // runs eagerly until latch.await() — isSyncing stays true
        manager.sync()  // no-op: isSyncing is true
        manager.sync()  // no-op: isSyncing is true

        assertEquals(1, callCount)

        latch.complete(Unit)  // let the first sync finish
    }

    @Test
    fun `sync emits Loading then Success on happy path`() = runTest {
        // Latch pauses the sync after Loading is emitted so we can observe it before Success
        val latch = CompletableDeferred<Unit>()
        coEvery { mockApiService.getPokemonList(limit = 1, offset = 0) } coAnswers {
            latch.await()
            countResponse(100)
        }
        coEvery { mockListEntryDao.count() } returns 100

        val manager = backgroundScope.syncManager()

        manager.sync()  // runs eagerly: emits Loading, then suspends at latch.await()

        assertTrue(manager.syncState.value is SyncState.Loading)

        latch.complete(Unit)  // let sync finish → emits Success

        assertTrue(manager.syncState.value is SyncState.Success)
    }

    // --- helpers ---

    private fun countResponse(count: Int) = PokemonListResponse(
        count = count,
        next = null,
        previous = null,
        results = emptyList()
    )

    private fun pageResponse(entries: List<PokemonEntryResponse>) = PokemonListResponse(
        count = entries.size,
        next = null,
        previous = null,
        results = entries
    )

    private fun fakeEntry(id: Int) = PokemonEntryResponse(
        name = "pokemon-$id",
        url = "https://pokeapi.co/api/v2/pokemon/$id/"
    )

    private fun fakePokemonResponse(id: Int) = PokemonResponse(
        id = id,
        name = "pokemon-$id",
        height = 10,
        weight = 100,
        types = emptyList(),
        stats = emptyList(),
        sprites = PokemonSprites(frontDefault = "https://example.com/$id.png", null),
        abilities = emptyList()
    )
}
