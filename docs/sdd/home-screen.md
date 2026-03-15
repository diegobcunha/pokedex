# SDD: Home Screen (Pokemon List)

## Feature: POKE-003 — Pokédex Home Screen

---

## Decisions Made

### Architecture
- **MVI pattern** with `PokemonListState` (object, stateless), `PokemonListIntent`, and `PokemonListEffect` as a separate sealed interface (not part of MviState hierarchy).
- **Side effects** use `Channel<PokemonListEffect>` exposed as `Flow` via `receiveAsFlow()` — ensures one-shot delivery (navigation, snackbar) without state duplication.
- **Paging flow** initialized as a cold `flow { emitAll(repository.getPokemonList()) }.cachedIn(viewModelScope)` to handle the `suspend` repository API while satisfying Paging3's `cachedIn` requirement for config-change survival.

### Navigation
- Pokemon ID is extracted from `PokemonEntryResponse.url` at the ViewModel layer: `url.trimEnd('/').substringAfterLast('/')`. This keeps the screen and navigation agnostic of URL parsing.
- `koinViewModel()` is used inside `pokemonGraph` to inject the ViewModel at the nav destination level.

### UI States
- Full-screen `CircularProgressIndicator` for `refresh is Loading`
- Full-screen "Retry" `TextButton` for `refresh is Error`
- `LazyColumn` with `Text` + `HorizontalDivider` for the happy path
- `SnackbarHost` via `Scaffold` for append errors

### Deprecated import fix
- `org.koin.androidx.viewmodel.dsl.viewModel` → `org.koin.core.module.dsl.viewModel` (Koin 4.x migration)

---

## Technical Features Implemented

| File | Purpose |
|------|---------|
| `PokemonListState.kt` | MVI state (`object`), intent (`SelectPokemon`), effect (`NavigateToDetail`, `ShowErrorSnackbar`) |
| `PokemonListViewModel.kt` | Exposes `pagingFlow` (cachedIn), processes `SelectPokemon` → `NavigateToDetail` effect |
| `PokemonListScreen.kt` | Full Paging3 UI: loading, error+retry, item list, snackbar, effect collection |
| `PokemonModule.kt` | Registers `PokemonListViewModel` with Koin `viewModel { }` |
| `PokemonNavGraph.kt` | Injects ViewModel via `koinViewModel()`, passes to screen |
| `libs.versions.toml` | Added `androidx-paging-compose` library entry |
| `build.gradle.kts` | Added `paging-compose` (impl) and `paging-testing` (test) dependencies |
| `PokemonListViewModelTest.kt` | 3 unit tests: ID extraction with/without trailing slash, pagingFlow collection |

---

## Current Status

**Complete.** The home screen is fully functional:
- Paginated Pokémon list loads from the PokéAPI
- All Paging3 load states (loading, error, success) are handled
- Tapping a Pokémon navigates to the detail screen
- Append errors surface via Snackbar
- Unit tests pass with 0 warnings

---

## Last Updated

2026-03-15
