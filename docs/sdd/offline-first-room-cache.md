# SDD: Offline-First — Room Cache

## Feature: POKE-008 — Offline-First with Room

---

## Decisions Made

### Cache Strategy
- **24-hour TTL** used for both the list and detail caches. The TTL for the list is driven by the oldest `createdAt` value in `remote_keys`, checked in `RemoteMediator.initialize()`. The TTL for the detail is stored as `lastFetched` on each `PokemonDetailEntity`.
- **Detail is cache-first with background refresh**: when a cached detail exists, it is emitted immediately so the UI renders without waiting for the network. A background fetch then runs only if the cache is stale (> 24h). The UI is updated only if the fresh data differs from the cached data.
- **List uses RemoteMediator**: the UI always reads from the Room `PagingSource`. The network is only the source of truth for writes into Room. This gives automatic offline support and config-change survival with no extra logic in the ViewModel.

### Repository API Change
- `getPokemonDetail` changed from `suspend fun … : Resource<PokemonResponse>` to `fun … : Flow<Resource<PokemonResponse>>`. This is required to support multiple emissions (cached → network) from the same call. The `getEvolutionChain` call was left as `suspend fun` since it has no caching requirement.

### DTO / Entity Boundary
- `PokemonDetailEntity` stores `types`, `stats`, and `abilities` as JSON strings (`List<String>`, `List<CachedStat>`, `List<String>` respectively) to avoid additional join tables while keeping the schema flat.
- The serialization helpers (`toDetailEntity`, `toPokemonResponse`) are **private** to `PokemonRepositoryImpl`. DTOs (`PokemonResponse`) never leave the datasource module.
- `PokemonListEntryEntity.types` is stored as a JSON `List<String>` (already sorted by slot at write time). Deserialization happens in `PokemonListEntryEntity.toDomain()` inside the mapper.

### RemoteMediator vs PagingSource
- `PokemonPagingSource` was **deleted**. The `RemoteMediator` pattern replaces it entirely: Room's DB-backed `PagingSource` feeds the UI, and `PokemonRemoteMediator` handles network fetch → DB write.
- The detail enrichment (image URL, types) that was previously in `PokemonPagingSource.load()` is now in `PokemonRemoteMediator.load()`, using the same `Semaphore` + `async/awaitAll` pattern for concurrent requests.

### KSP over KAPT
- KSP (`com.google.devtools.ksp`, version `2.3.4`) is used for Room annotation processing in the `datasource` module. The `room.schemaLocation` annotation processor argument is not set (schema export disabled), which is acceptable for the current development stage.

### ViewModel Dependencies
- `PokemonListViewModel` now takes `PokedexDatabase` directly (in addition to `PokemonApiService` and `DispatchersProvider`) to construct the `Pager` with `RemoteMediator` inline. This is a pragmatic choice that avoids an extra abstraction layer for what is essentially wiring code.

### Testing Decision — RemoteMediator REFRESH
- The `load(REFRESH)` scenario (verifying that `clearAll()` + `insertAll()` are called inside the transaction) was **intentionally excluded** from unit tests. `database.withTransaction { }` is a suspend extension function from `room-ktx` that executes real Room internals on a mock database object, making it impossible to mock reliably at the JVM unit test level. This scenario is a candidate for an instrumented test using an in-memory Room database.

---

## Technical Features Implemented

### `datasource` module
| File | Description |
|---|---|
| `db/entity/PokemonListEntryEntity.kt` | Room entity for list entries; `types` stored as JSON string |
| `db/entity/RemoteKeyEntity.kt` | Paging remote keys with `createdAt` for TTL |
| `db/entity/PokemonDetailEntity.kt` | Room entity for detail; `types`, `stats`, `abilities` stored as JSON strings; `lastFetched` for TTL |
| `db/dao/PokemonListEntryDao.kt` | `pagingSource()`, `insertAll()`, `clearAll()` |
| `db/dao/RemoteKeyDao.kt` | `insertAll()`, `remoteKeyFor()`, `oldestCreatedAt()`, `clearAll()` |
| `db/dao/PokemonDetailDao.kt` | `getById()`, `insert()` |
| `db/PokedexDatabase.kt` | Room database v1 with all three entities |
| `di/DatasourceModule.kt` | Added `PokedexDatabase` singleton and DAO singletons |
| `repository/PokemonRepository.kt` | `getPokemonDetail` signature changed to `Flow` |
| `repository/PokemonRepositoryImpl.kt` | Cache-first + TTL + emit-on-change logic; private `toDetailEntity` / `toPokemonResponse` helpers |

### `feature:pokemon` module
| File | Description |
|---|---|
| `data/paging/PokemonRemoteMediator.kt` | `initialize()` with 24h TTL; `load()` with PREPEND/REFRESH/APPEND handling; detail enrichment with semaphore |
| `data/paging/PokemonPagingSource.kt` | **Deleted** — replaced by `RemoteMediator` + Room `PagingSource` |
| `domain/mapper/PokemonMapper.kt` | Added `PokemonListEntryEntity.toDomain()` with JSON deserialization |
| `presentation/list/PokemonListViewModel.kt` | Uses `Pager` with `RemoteMediator`; emits `PagingData<PokemonEntry>` via DB-backed source |
| `presentation/detail/PokemonDetailViewModel.kt` | Collects `Flow<Resource<…>>`; shows cached state immediately, updates only when data changes |
| `di/PokemonModule.kt` | Updated `PokemonListViewModel` to receive `PokedexDatabase` |

---

## Current Status

**Complete.** All code is implemented and all unit tests pass.

- `./gradlew assembleDebug` — passes
- `./gradlew test` — passes (20 unit tests across datasource and feature:pokemon)
- Manual verification (airplane mode) — pending

**Known gap:** The `load(REFRESH)` persistence path (clear + insert inside `withTransaction`) is not covered by unit tests. It should be verified with an instrumented test against an in-memory Room database.

---

## Last Updated

2026-03-18
