# SDD: Count-Based Delta Sync

## Feature Overview

Replaced the 24-hour TTL caching strategy with a count-based delta sync for the Pokemon list screen. On every app launch the app compares the API's total pokemon count against the local DB count, fetches only the delta (new entries), and stores them atomically.

---

## Decisions Made

### Count comparison instead of TTL
The PokeAPI dataset only grows (new pokemon are added, none removed). A count comparison is cheaper than a timestamp check: one `GET /pokemon?limit=1&offset=0` call returns only the `count` field, and `pokemonListEntryDao.count()` is a single SQL `COUNT(*)`. No unnecessary re-fetching of already-cached data.

### `TransactionRunner` injectable interface
`db.withTransaction` is a Room extension function (`@JvmStatic`) that cannot be mocked with `mockkStatic` reliably across all JVM test configurations. Introducing `TransactionRunner` as a `fun interface` lets production code wrap `db.withTransaction` while tests inject a passthrough `{ block -> block() }`. No reflection hacks, no test infrastructure complexity.

### `coroutineScope` for parallel fetches (not `withContext`)
`withContext + async + awaitAll` can swallow exceptions: when a child `async` throws, the parent scope is cancelled, and `awaitAll()` may throw `CancellationException` instead of the original exception. Using `coroutineScope { async { }.awaitAll() }` ensures the original exception from any failed child propagates correctly to the caller.

### `retryDelayMs` injectable in `PokemonSyncManager`
The retry backoff delay (`500ms * attempt`) is extracted as a constructor parameter (`retryDelayMs = DEFAULT_RETRY_DELAY_MS`). Tests pass `retryDelayMs = 0L` so retry logic is exercised without needing virtual time advancement.

### `CompletableDeferred` latch in tests for mid-flight assertions
With `UnconfinedTestDispatcher`, coroutines run eagerly: a sync that triggers no delays completes before `sync()` returns. To test `isSyncing` deduplication or intermediate state (`SyncState.Loading`), tests use a `CompletableDeferred<Unit>` latch in a `coAnswers` block. The latch suspends the first API call, leaving `isSyncing = true` and `syncState = Loading` observable, before being completed to let the sync finish.

### Removed `PokemonRemoteMediator`
After delta sync, all pokemon data is in the local DB. Paging 3's `RemoteMediator` served a dual role (page loading + refresh). With count-based sync owning the refresh, a local-only `Pager(PagingConfig(pageSize=30)) { listEntryDao.pagingSource() }` is sufficient. Activated lazily via `syncState.filter { Success }.flatMapLatest { Pager(...).flow }`.

### Singleton `CoroutineScope` for sync (Koin `named("SyncScope")`)
The sync scope outlives any single screen. A `CoroutineScope(SupervisorJob() + Dispatchers.IO)` registered as a singleton in Koin ensures that navigating away from the list screen while sync is in progress does not cancel the sync job. `SupervisorJob` isolates failures within child coroutines.

### Two-phase sync marker (`SyncStateEntity`)
Writing `PENDING` (with `previousCount`) before any network calls and `COMPLETE` after all pages are saved allows crash recovery: on startup, a `PENDING` row indicates an interrupted sync. If the sync fails mid-way, orphaned entries added beyond `previousCount` are deleted via `listEntryDao.deleteEntriesAfterOffset(previousCount)` before re-throwing.

---

## Technical Features Implemented

| Component | Location |
|---|---|
| `SyncStateEntity` (Room entity) | `datasource/.../db/entity/SyncStateEntity.kt` |
| `SyncStateDao` | `datasource/.../db/dao/SyncStateDao.kt` |
| `SyncState` (sealed class) | `datasource/.../sync/SyncState.kt` |
| `TransactionRunner` (fun interface) | `datasource/.../sync/TransactionRunner.kt` |
| `PokemonSyncManager` | `datasource/.../sync/PokemonSyncManager.kt` |
| `PokemonListState` (sealed class) | `feature/pokemon/.../list/PokemonListState.kt` |
| `PokemonListViewModel` (refactored) | `feature/pokemon/.../list/PokemonListViewModel.kt` |
| `PokemonListScreen` (refactored) | `feature/pokemon/.../list/PokemonListScreen.kt` |
| Room migration v2 → v3 | `datasource/.../db/PokedexMigrations.kt` |
| `PokemonSyncManagerTest` (8 tests) | `datasource/.../sync/PokemonSyncManagerTest.kt` |

### Removed
- `PokemonRemoteMediator`
- `RemoteKeyEntity` / `RemoteKeyDao`
- 24-hour TTL constant in `PokemonRepositoryImpl`

### Sync parameters
- Page size: 30 items
- Max concurrent requests (semaphore): 10
- Max retry attempts per page: 3
- Retry backoff: `500ms × attempt` (0ms in tests)

---

## Current Status

**Complete.** All unit tests pass (full suite green). Debug build succeeds.

### State machine
```
Idle → Loading → Success   (list renders)
             ↘ Error      (retry screen with Retry button)
```

### Error recovery
- Any page that fails 3 retries fails the entire sync
- Orphaned entries beyond the pre-sync DB count are deleted atomically
- User sees full-screen error with a Retry button (`PokemonListIntent.Retry`)

---

## Last Updated

2026-04-11
