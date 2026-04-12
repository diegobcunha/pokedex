package com.diegocunha.pokedex.feature.pokemon.presentation.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.sqlite.db.SupportSQLiteQuery
import app.cash.turbine.test
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.dao.PokemonListEntryDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.sync.PokemonSyncManager
import com.diegocunha.pokedex.datasource.sync.SyncState
import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    private val fakeSyncState = MutableStateFlow<SyncState>(SyncState.Idle)
    private val mockSyncManager = mockk<PokemonSyncManager>(relaxed = true) {
        every { syncState } returns fakeSyncState
    }
    private val mockListEntryDao = mockk<PokemonListEntryDao>(relaxed = true) {
        every { pagingSource() } returns FakeEmptyPagingSource()
        every { pagingSourceFiltered(any()) } returns FakeEmptyPagingSource()
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = PokemonListViewModel(mockSyncManager, mockListEntryDao, testDispatchers)

    @Test
    fun `init triggers sync on SyncManager`() = runTest {
        viewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        verify(exactly = 1) { mockSyncManager.sync() }
    }

    @Test
    fun `sync Loading state maps to PokemonListState Loading`() = runTest {
        val vm = viewModel()
        fakeSyncState.value = SyncState.Loading
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value is PokemonListState.Loading)
    }

    @Test
    fun `sync Success state maps to PokemonListState Success`() = runTest {
        val vm = viewModel()
        fakeSyncState.value = SyncState.Success
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value is PokemonListState.Success)
    }

    @Test
    fun `sync Error state maps to PokemonListState Error with exception`() = runTest {
        val vm = viewModel()
        val exception = RuntimeException("Sync failed")
        fakeSyncState.value = SyncState.Error(exception)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = vm.state.value
        assertTrue(state is PokemonListState.Error)
        assertEquals(exception, (state as PokemonListState.Error).exception)
    }

    @Test
    fun `SelectPokemon intent emits NavigateToDetail effect`() = runTest {
        val vm = viewModel()

        vm.effects.test {
            vm.sendIntent(PokemonListIntent.SelectPokemon(id = "25"))
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PokemonListEffect.NavigateToDetail("25"), awaitItem())
        }
    }

    @Test
    fun `Retry intent calls sync on SyncManager`() = runTest {
        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.sendIntent(PokemonListIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        // sync() called once on init + once on Retry
        verify(exactly = 2) { mockSyncManager.sync() }
    }

    @Test
    fun `pagingFlow collects without error when sync succeeds`() = runTest {
        val vm = viewModel()
        fakeSyncState.value = SyncState.Success
        testDispatcher.scheduler.advanceUntilIdle()

        vm.pagingFlow.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- Search & Filter tests ---

    @Test
    fun `initial searchFilter is empty with no active filter`() = runTest {
        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val filter = vm.searchFilter.value
        assertEquals("", filter.query)
        assertTrue(filter.selectedTypes.isEmpty())
        assertFalse(filter.isActive)
    }

    @Test
    fun `UpdateQuery intent updates searchFilter query immediately`() = runTest {
        val vm = viewModel()

        vm.sendIntent(PokemonListIntent.UpdateQuery("char"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("char", vm.searchFilter.value.query)
    }

    @Test
    fun `UpdateQuery marks filter as active when query is not blank`() = runTest {
        val vm = viewModel()

        vm.sendIntent(PokemonListIntent.UpdateQuery("pikachu"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.searchFilter.value.isActive)
    }

    @Test
    fun `ToggleTypeFilter adds type to selectedTypes`() = runTest {
        val vm = viewModel()

        vm.sendIntent(PokemonListIntent.ToggleTypeFilter(PokemonType.FIRE))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(PokemonType.FIRE in vm.searchFilter.value.selectedTypes)
        assertTrue(vm.searchFilter.value.isActive)
    }

    @Test
    fun `ToggleTypeFilter removes type when already selected`() = runTest {
        val vm = viewModel()

        vm.sendIntent(PokemonListIntent.ToggleTypeFilter(PokemonType.FIRE))
        testDispatcher.scheduler.advanceUntilIdle()
        vm.sendIntent(PokemonListIntent.ToggleTypeFilter(PokemonType.FIRE))
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(PokemonType.FIRE in vm.searchFilter.value.selectedTypes)
    }

    @Test
    fun `ToggleTypeFilter supports multiple selected types`() = runTest {
        val vm = viewModel()

        vm.sendIntent(PokemonListIntent.ToggleTypeFilter(PokemonType.FIRE))
        vm.sendIntent(PokemonListIntent.ToggleTypeFilter(PokemonType.FLYING))
        testDispatcher.scheduler.advanceUntilIdle()

        val types = vm.searchFilter.value.selectedTypes
        assertTrue(PokemonType.FIRE in types)
        assertTrue(PokemonType.FLYING in types)
    }

    @Test
    fun `ClearFilters resets query and selected types`() = runTest {
        val vm = viewModel()

        vm.sendIntent(PokemonListIntent.UpdateQuery("bulba"))
        vm.sendIntent(PokemonListIntent.ToggleTypeFilter(PokemonType.GRASS))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.sendIntent(PokemonListIntent.ClearFilters)
        testDispatcher.scheduler.advanceUntilIdle()

        val filter = vm.searchFilter.value
        assertEquals("", filter.query)
        assertTrue(filter.selectedTypes.isEmpty())
        assertFalse(filter.isActive)
    }

    @Test
    fun `name query debounce triggers pagingSourceFiltered after 2 seconds`() = runTest {
        val vm = viewModel()
        fakeSyncState.value = SyncState.Success
        testDispatcher.scheduler.advanceUntilIdle()

        vm.pagingFlow.test {
            awaitItem() // initial emission — empty filter

            vm.sendIntent(PokemonListIntent.UpdateQuery("char"))

            // Before debounce window — pagingFlow not yet triggered with new filter
            advanceTimeBy(1_000)

            // After debounce window
            advanceTimeBy(1_500)
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }

        verify { mockListEntryDao.pagingSourceFiltered(any<SupportSQLiteQuery>()) }
    }

    @Test
    fun `type filter triggers pagingSourceFiltered immediately without debounce`() = runTest {
        val vm = viewModel()
        fakeSyncState.value = SyncState.Success
        testDispatcher.scheduler.advanceUntilIdle()

        vm.pagingFlow.test {
            awaitItem() // initial emission — empty filter

            vm.sendIntent(PokemonListIntent.ToggleTypeFilter(PokemonType.WATER))
            testDispatcher.scheduler.advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }

        verify { mockListEntryDao.pagingSourceFiltered(any<SupportSQLiteQuery>()) }
    }
}
