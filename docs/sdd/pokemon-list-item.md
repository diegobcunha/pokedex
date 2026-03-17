# SDD: Pokemon List Item

---

## v1 — Basic Text List Item (2026-03-15)

### Decisions Made

#### Display format
- Pokemon name and ID are shown as `#<id> <name>` (e.g., `#1 Bulbasaur`).
- ID is used as-is, with no zero-padding.
- Name is capitalized via `replaceFirstChar { it.uppercase() }` — locale-safe and idempotent on already-capitalized names.

#### Composable extraction
- `PokemonListItem` is extracted as a private composable in `PokemonListScreen.kt`.
- No separate file was created — the component is scoped to the list screen and not shared elsewhere.
- Existing typography and click handling were preserved unchanged.

#### Previewability of `PokemonListScreen`
- `PokemonListScreen` originally accepted a `ViewModel` directly, making it impossible to preview.
- A private stateless `PokemonListScreenContent` composable was extracted, accepting `LazyPagingItems`, `SnackbarHostState`, and callbacks.
- `PokemonListScreen` delegates to it; this is the only structural change to the screen.
- Three previews cover the three distinct UI states: list, loading, and error+retry.

#### No unit tests
- Compose views are excluded from coverage per project rules. Preview-only verification was agreed upon.

### Technical Features Implemented

| File | Change |
|------|--------|
| `PokemonListScreen.kt` | Extracted `PokemonListItem` composable with `#id name` format and capitalization |
| `PokemonListScreen.kt` | Extracted `PokemonListScreenContent` stateless composable to enable screen-level previews |
| `PokemonListScreen.kt` | Added `@Preview` for `PokemonListItem` (single item) |
| `PokemonListScreen.kt` | Added `@Preview` for `PokemonListScreenContent` in List, Loading, and Error states |

### Status
Complete.

---

## v2 — Enriched Card List Item (2026-03-15)

### Decisions Made

#### Parallel detail enrichment in PagingSource
- The list endpoint (`/pokemon?limit=20&offset=N`) only returns `name` and `url` per entry — no image or types.
- After fetching a page, the `PokemonPagingSource.load()` method fans out parallel `getPokemonDetail(id)` calls for every entry in the page using `async`/`awaitAll`.
- A `Semaphore(params.loadSize)` gates concurrency to match the page size, preventing runaway parallelism on large initial loads.
- The entire page is held until all parallel calls resolve (`awaitAll`) before emitting to the UI — users see a loading spinner, then the fully-enriched page.
- `withContext(dispatchers.io())` wraps the fan-out to run on the IO dispatcher, keeping the dispatcher injectable for testing.

#### Per-item failure fallback
- Each detail call is wrapped in `runCatching { ... }.getOrNull()`.
- If a detail call fails, the item falls back to `entry.toDomain()` — `imageUrl = null`, `types = emptyList()` — rendered as a card with the UNKNOWN type color and no image.
- The list-fetch failure path is unchanged: a wholesale exception still returns `LoadResult.Error`.

#### initialLoadSize fix
- `PagingConfig` previously omitted `initialLoadSize`, defaulting to `pageSize * 3 = 60`.
- This was fixed to `initialLoadSize = 20` — consistent with subsequent page loads and the stated semaphore requirement.

#### PokemonEntry model extension
- `imageUrl: String?` and `types: List<String>` were added to `PokemonEntry` with default values (`null` / `emptyList()`).
- Existing test assertions and preview instantiations required no changes.

#### New mapper overload
- `PokemonEntryResponse.toDomain(detail: PokemonResponse)` added as a new overload alongside the existing zero-arg version.
- Types are sorted by `slot` before mapping to ensure the primary type (slot = 1) is always `types[0]`, which drives the card background color.

#### PokemonType relocation
- `PokemonType` enum was in `presentation/detail/components/` — a detail-screen-private package.
- Moved to `presentation/common/PokemonType.kt` to allow sharing between list and detail screens.
- The original `detail/components/PokemonType.kt` is retained as a `typealias` to preserve compile compatibility for `TypeChip` and `PokemonDetailScreen` without import changes.

#### Card UI design
- `PokemonListItem` replaced with a `Card` composable.
- Card background color = `PokemonType.fromName(types.firstOrNull()).color`.
- Layout: `Row` with an `AsyncImage` (80 dp, Coil) on the left and the capitalized Pokémon name on the right in white `titleMedium` text.
- `HorizontalDivider` removed — cards provide visual separation.
- Layout remains `LazyColumn` (not a grid).

#### DispatchersProvider injection
- `DispatchersProvider` injected into `PokemonPagingSource` and `PokemonListViewModel` to keep coroutine dispatchers swappable in tests.
- Koin binding updated from `get()` to `get(), get()`.

### Technical Features Implemented

| File | Change |
|------|--------|
| `domain/model/PokemonEntry.kt` | Added `imageUrl: String?` and `types: List<String>` with defaults |
| `domain/mapper/PokemonMapper.kt` | Added `PokemonEntryResponse.toDomain(detail: PokemonResponse)` overload |
| `data/paging/PokemonPagingSource.kt` | Added `DispatchersProvider`; rewrote `load()` with parallel enrichment via `async`/`awaitAll`/`Semaphore` |
| `presentation/list/PokemonListViewModel.kt` | Added `DispatchersProvider`; fixed `initialLoadSize = 20` |
| `di/PokemonModule.kt` | Updated `PokemonListViewModel` Koin binding to inject `DispatchersProvider` |
| `presentation/common/PokemonType.kt` | New file — canonical home for `PokemonType` enum |
| `presentation/detail/components/PokemonType.kt` | Replaced with `typealias` pointing to `presentation/common/PokemonType` |
| `presentation/list/PokemonListScreen.kt` | Replaced `PokemonListItem` with type-colored card; added Coil `AsyncImage`; removed `HorizontalDivider`; updated previews |
| `PokemonPagingSourceTest.kt` | Updated constructor; added 4 new enrichment test cases |
| `PokemonListViewModelTest.kt` | Updated constructor to pass `DispatchersProvider` test double |

### Current Status
Complete. The list screen now fetches image and primary type for each Pokémon via parallel detail calls before rendering. Each item is displayed as a type-colored card with the Pokémon sprite and name.

### Last Updated
2026-03-15
