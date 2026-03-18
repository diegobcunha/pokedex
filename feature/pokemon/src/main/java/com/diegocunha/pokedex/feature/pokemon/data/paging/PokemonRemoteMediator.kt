package com.diegocunha.pokedex.feature.pokemon.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.PokedexDatabase
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.db.entity.RemoteKeyEntity
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val TTL_MS = 24 * 60 * 60 * 1000L

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val apiService: PokemonApiService,
    private val database: PokedexDatabase,
    private val dispatchers: DispatchersProvider
) : RemoteMediator<Int, PokemonListEntryEntity>() {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun initialize(): InitializeAction {
        val oldestCreatedAt = database.remoteKeyDao().oldestCreatedAt()
        return if (oldestCreatedAt != null && System.currentTimeMillis() - oldestCreatedAt < TTL_MS) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonListEntryEntity>
    ): MediatorResult {
        return try {
            val offset = when (loadType) {
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.REFRESH -> 0
                LoadType.APPEND -> {
                    val lastItem = state.pages.lastOrNull()?.data?.lastOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val remoteKey = database.remoteKeyDao().remoteKeyFor(lastItem.id)
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val pageSize = state.config.pageSize
            val response = apiService.getPokemonList(pageSize, offset)

            val semaphore = Semaphore(pageSize)
            val entries = withContext(dispatchers.io()) {
                response.results.map { entry ->
                    async {
                        semaphore.withPermit {
                            val numericId = entry.url.trimEnd('/').substringAfterLast('/').toInt()
                            val detail = runCatching { apiService.getPokemonDetail(numericId) }.getOrNull()
                            val id = numericId.toString()
                            PokemonListEntryEntity(
                                id = id,
                                name = entry.name,
                                imageUrl = detail?.sprites?.frontDefault,
                                types = json.encodeToString(
                                    ListSerializer(String.serializer()),
                                    detail?.types?.sortedBy { it.slot }?.map { it.type.name }
                                        ?: emptyList()
                                )
                            )
                        }
                    }
                }.awaitAll()
            }

            val remoteKeys = entries.map { entry ->
                RemoteKeyEntity(
                    pokemonId = entry.id,
                    prevKey = if (offset == 0) null else offset - pageSize,
                    nextKey = if (response.next == null) null else offset + pageSize
                )
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.pokemonListEntryDao().clearAll()
                    database.remoteKeyDao().clearAll()
                }
                database.pokemonListEntryDao().insertAll(entries)
                database.remoteKeyDao().insertAll(remoteKeys)
            }

            MediatorResult.Success(endOfPaginationReached = response.next == null)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
