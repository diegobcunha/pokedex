# SDD Documentation — `network-call`

**Last Updated:** 2026-03-15
**Status:** Completed

---

## Decisions Made

| Decision | Rationale |
|---|---|
| `Resource<T>` sealed class lives in `:core` | Shared result type usable by all feature modules without creating a datasource dependency |
| `Result<T>.toResource()` extension co-located with `Resource<T>` in `:core` | Logically belongs next to the type it converts into; avoids forcing datasource on core tests |
| `safeApiCall` lives in `:datasource` | Wraps the network layer — only datasource consumers need it |
| `safeApiCall` receives `DispatchersProvider` instead of a raw `CoroutineDispatcher` | Enables clean dispatcher injection in tests without coroutine test boilerplate |
| `DispatchersProvider` interface lives in `:core` | Dispatcher abstraction is a cross-cutting concern; feature modules and datasource both need it |
| Paging3 (`PagingSource` + `Pager`) used for the pokemon list | Handles pagination, load states, and retries out of the box; avoids manual offset tracking in the ViewModel |
| `PokemonRepository` interface declared in `:datasource` | Feature modules depend on the interface, not the implementation — enables future mocking in feature-level tests |
| Response models use `@Serializable` with `@SerialName` for snake_case fields | PokeAPI returns snake_case JSON; field renaming is explicit and not reliant on global configuration |
| `BuildConfig.DEBUG` used in `DatasourceModule` for OkHttp logging | Prevents logging interceptor from running in release builds without requiring a manual flag |

---

## Technical Features Implemented

### `:core`
- `Resource<T>` — sealed class with `Success<T>`, `Error`, and `Loading` states
- `Result<T>.toResource()` — extension that maps Kotlin's `Result` to `Resource`
- `DispatchersProvider` — interface abstracting `io()` and `main()` dispatchers
- `DispatchersProviderImpl` — production implementation backed by `Dispatchers.IO` / `Dispatchers.Main`

### `:datasource`
- `SafeApiCall.kt` — `safeApiCall` suspend wrapper: dispatches on `DispatchersProvider.io()`, wraps exceptions via `runCatching().toResource()`
- `PokemonApiService` — Retrofit interface with three endpoints:
  - `GET pokemon` — paginated list (`limit` + `offset`)
  - `GET pokemon/{id}` — pokemon detail
  - `GET evolution-chain/{id}` — evolution chain
- Response models (`model` package):
  - `PokemonListResponse` / `PokemonEntryResponse`
  - `PokemonResponse` with nested `PokemonTypeSlot`, `PokemonStatSlot`, `PokemonSprites`, `PokemonAbilitySlot` and their inner types
  - `EvolutionChainResponse` with recursive `ChainLink` and `NamedResource`
- `PokemonPagingSource` — offset-based `PagingSource<Int, PokemonEntryResponse>`
- `PokemonRepository` — interface exposing `getPokemonList(): Flow<PagingData<PokemonEntryResponse>>`, `getPokemonDetail`, `getEvolutionChain`
- `PokemonRepositoryImpl` — concrete implementation wiring `Pager`, `PokemonPagingSource`, and `safeApiCall`
- `DatasourceModule` — updated Koin bindings for `PokemonApiService` and `PokemonRepository`

### Gradle
- `paging = "3.3.6"` added to version catalog
- `androidx-paging-runtime` and `androidx-paging-testing` added to version catalog
- `:datasource` gains dependencies: `:core`, `paging-runtime`, `kotlinx-coroutines-android`; test deps: `paging-testing`, `turbine`

---

## Test Coverage

| Test file | Cases |
|---|---|
| `core/.../ResourceTest` | `toResource()` success + failure; `Success`, `Error`, `Loading` construction |
| `datasource/.../SafeApiCallTest` | Success path, exception path, null value |
| `datasource/.../PokemonPagingSourceTest` | First page (null prevKey), last page (null nextKey), subsequent page (prevKey set), error case |
| `datasource/.../PokemonRepositoryImplTest` | `getPokemonDetail` success + error; `getEvolutionChain` success + error |

---

## Current Status

| Component | Status |
|---|---|
| `Resource<T>` + `toResource()` | Complete |
| `DispatchersProvider` | Complete |
| `safeApiCall` | Complete |
| Response models | Complete |
| `PokemonApiService` | Complete |
| `PokemonPagingSource` | Complete |
| `PokemonRepository` + `PokemonRepositoryImpl` | Complete |
| DI wiring | Complete |
| Unit tests | Complete — all passing |
