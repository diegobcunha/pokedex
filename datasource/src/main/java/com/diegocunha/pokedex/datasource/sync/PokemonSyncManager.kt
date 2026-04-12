package com.diegocunha.pokedex.datasource.sync

import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.dao.PokemonListEntryDao
import com.diegocunha.pokedex.datasource.db.dao.SyncStateDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.db.entity.SyncStateEntity
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.ceil

class PokemonSyncManager(
    private val apiService: PokemonApiService,
    private val listEntryDao: PokemonListEntryDao,
    private val syncStateDao: SyncStateDao,
    private val transactionRunner: TransactionRunner,
    private val syncScope: CoroutineScope,
    private val dispatchers: DispatchersProvider,
    private val retryDelayMs: Long = DEFAULT_RETRY_DELAY_MS
) {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val isSyncing = AtomicBoolean(false)
    private val json = Json { ignoreUnknownKeys = true }

    fun sync() {
        if (!isSyncing.compareAndSet(false, true)) return

        syncScope.launch(dispatchers.io()) {
            try {
                _syncState.value = SyncState.Loading
                performSync()
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e)
            } finally {
                isSyncing.set(false)
            }
        }
    }

    private suspend fun performSync() {
        val apiCount = apiService.getPokemonList(limit = 1, offset = 0).count
        val dbCount = listEntryDao.count()

        if (apiCount <= dbCount) return

        val previousCount = dbCount
        transactionRunner {
            syncStateDao.upsert(
                SyncStateEntity(
                    status = SYNC_STATUS_PENDING,
                    previousCount = previousCount
                )
            )
        }

        try {
            fetchAndStoreDelta(fromOffset = previousCount, apiCount = apiCount)
        } catch (e: Exception) {
            transactionRunner {
                listEntryDao.deleteEntriesAfterOffset(previousCount)
            }
            throw e
        }

        transactionRunner {
            syncStateDao.upsert(
                SyncStateEntity(
                    status = SYNC_STATUS_COMPLETE,
                    previousCount = previousCount
                )
            )
        }
    }

    private suspend fun fetchAndStoreDelta(fromOffset: Int, apiCount: Int) {
        val delta = apiCount - fromOffset
        val pageCount = ceil(delta.toDouble() / PAGE_SIZE).toInt()
        val semaphore = Semaphore(MAX_CONCURRENT_REQUESTS)

        coroutineScope {
            (0 until pageCount).map { pageIndex ->
                async(dispatchers.io()) {
                    semaphore.withPermit {
                        val offset = fromOffset + (pageIndex * PAGE_SIZE)
                        val limit = minOf(PAGE_SIZE, apiCount - offset)
                        val entries = fetchPageWithRetry(offset = offset, limit = limit)
                        transactionRunner { listEntryDao.insertAll(entries) }
                    }
                }
            }.awaitAll()
        }
    }

    private suspend fun fetchPageWithRetry(offset: Int, limit: Int): List<PokemonListEntryEntity> {
        var lastException: Exception? = null
        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                return fetchPage(offset = offset, limit = limit)
            } catch (e: Exception) {
                lastException = e
                if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                    delay(retryDelayMs * (attempt + 1))
                }
            }
        }
        throw lastException ?: IllegalStateException("Sync failed after $MAX_RETRY_ATTEMPTS attempts")
    }

    private suspend fun fetchPage(offset: Int, limit: Int): List<PokemonListEntryEntity> {
        val listResponse = apiService.getPokemonList(limit = limit, offset = offset)
        val detailSemaphore = Semaphore(MAX_CONCURRENT_REQUESTS)

        return coroutineScope {
            listResponse.results.map { entry ->
                async(dispatchers.io()) {
                    detailSemaphore.withPermit {
                        val numericId = entry.url.trimEnd('/').substringAfterLast('/').toInt()
                        val detail = runCatching { apiService.getPokemonDetail(numericId) }.getOrNull()
                        PokemonListEntryEntity(
                            id = numericId.toString(),
                            name = entry.name,
                            imageUrl = detail?.sprites?.frontDefault,
                            types = json.encodeToString(
                                ListSerializer(String.serializer()),
                                detail?.types?.sortedBy { it.slot }?.map { it.type.name } ?: emptyList()
                            )
                        )
                    }
                }
            }.awaitAll()
        }
    }

    companion object {
        private const val PAGE_SIZE = 30
        private const val MAX_CONCURRENT_REQUESTS = 10
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val DEFAULT_RETRY_DELAY_MS = 500L
        private const val SYNC_STATUS_PENDING = "PENDING"
        private const val SYNC_STATUS_COMPLETE = "COMPLETE"
    }
}
