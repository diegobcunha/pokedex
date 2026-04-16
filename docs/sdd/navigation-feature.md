# SDD: Navigation Feature

## Decisions Made

### Original Setup (Navigation 2 — 2026-03-14)

- **Navigation strategy:** `NavGraphBuilder` extensions per feature module. Each feature module owns its own routes and graph wiring, keeping navigation logic colocated with the feature.
- **Library:** Jetpack Compose Navigation (`androidx.navigation:navigation-compose:2.9.7`)
- **Dependency ownership:** `navigation-compose` declared once in `core-ui` as `api()`, exposing it transitively to all feature modules and `app`.
- **Route format:** String-based routes with path parameters (e.g., `pokemon/detail/{pokemonId}`), using `navArgument` for type-safe argument extraction.
- **Argument type:** `pokemonId` passed as `NavType.StringType` between all screens.

---

### Migration to Navigation 3 (2026-04-15)

- **Library:** Migrated to `androidx.navigation3:navigation3-ui:1.0.0-alpha04` — Google's complete redesign of the Compose navigation library.
- **Route keys:** String constants replaced with typed Kotlin data classes/objects implementing `NavKey`. Arguments are now fields on the key object, eliminating all string path parsing.
- **NavGraph pattern:** `NavGraphBuilder` extensions no longer exist in Navigation 3. Feature modules now expose `EntryProviderBuilder<NavKey>` extension functions that are called inside `entryProvider { }` in the app module. This preserves the modular ownership model.
- **Back stack:** `rememberNavController()` + `NavHost` replaced by `rememberNavBackStack()` + `NavDisplay`. Navigation is now explicit list manipulation (`backStack.add(key)` / `backStack.removeLastOrNull()`), removing implicit NavController side-effects.
- **ViewModel injection:** No change — Koin `parametersOf(key.pokemonId)` is used identically; `key.pokemonId` replaces `backStackEntry.arguments?.getString("pokemonId")`.
- **Dependency ownership:** Maintained in `core-ui` as `api()`, no change to module dependency graph.
- **Architecture preserved:** Navigation decisions (what to push/pop) remain in the app module via callbacks. Feature modules own their keys and entry registrations only.

---

## Technical Features Implemented

### Navigation 3 Migration

| File | Change |
|------|--------|
| `gradle/libs.versions.toml` | Replaced `navigation = "2.9.7"` + `androidx-navigation-compose` with `navigation3 = "1.0.0-alpha04"` + `androidx-navigation3-ui` |
| `core-ui/build.gradle.kts` | `api(libs.androidx.navigation.compose)` → `api(libs.androidx.navigation3.ui)` |
| `feature/pokemon/.../navigation/PokemonRoutes.kt` | `object PokemonRoutes` (strings) → `data object PokemonList : NavKey` and `data class PokemonDetail(val pokemonId: String) : NavKey` |
| `feature/pokemon/.../navigation/PokemonNavGraph.kt` | `NavGraphBuilder.pokemonGraph()` → `EntryProviderBuilder<NavKey>.pokemonEntries()` |
| `feature/evolutions/.../navigation/EvolutionRoutes.kt` | `object EvolutionRoutes` (strings) → `data class EvolutionDestination(val pokemonId: String) : NavKey` |
| `feature/evolutions/.../navigation/EvolutionNavGraph.kt` | `NavGraphBuilder.evolutionGraph()` → `EntryProviderBuilder<NavKey>.evolutionEntries()` |
| `app/.../MainActivity.kt` | `rememberNavController()` + `NavHost` → `rememberNavBackStack(PokemonList)` + `NavDisplay` |

### Navigation Flow

```
PokemonListScreen
    │  backStack.add(PokemonDetail(id))
    ▼
PokemonDetailScreen
    │  backStack.add(EvolutionDestination(id))   [future]
    ▼
EvolutionScreen
    │  backStack.removeLastOrNull()
    ▼
(previous screen)
```

### Key API Comparison

| Concept | Navigation 2 | Navigation 3 |
|---------|-------------|--------------|
| Route definition | `"pokemon/detail/{pokemonId}"` (String) | `data class PokemonDetail(val pokemonId: String) : NavKey` |
| Graph builder | `NavGraphBuilder.pokemonGraph()` | `EntryProviderBuilder<NavKey>.pokemonEntries()` |
| Host composable | `NavHost(navController, startDestination)` | `NavDisplay(backStack, onBack, entryProvider)` |
| Navigate forward | `navController.navigate(PokemonRoutes.detail(id))` | `backStack.add(PokemonDetail(id))` |
| Navigate back | `navController.popBackStack()` | `backStack.removeLastOrNull()` |
| Extract argument | `backStackEntry.arguments?.getString("pokemonId")` | `key.pokemonId` (direct field access) |
| State holder | `rememberNavController()` | `rememberNavBackStack(startKey)` |

---

## Current Status

Navigation 3 migration is complete. All screens navigate correctly with type-safe route keys. No string path parameters remain in the navigation layer. Unit tests pass with no navigation-related changes required (navigation is not directly unit-tested).

> **Note:** `navigation3-ui:1.0.0-alpha04` is in alpha. The API surface may change in future releases. No production stability guarantees.

---

## Changelog

| Date | Change |
|------|--------|
| 2026-03-14 | Initial navigation scaffold with Navigation 2, string routes, `NavGraphBuilder` extensions |
| 2026-03-14 | Updated navigation version to 2.9.7, moved dependency ownership to `core-ui` |
| 2026-04-15 | Migrated to Navigation 3 (`navigation3-ui:1.0.0-alpha04`): typed `NavKey` route objects, `EntryProviderBuilder` extensions, `NavDisplay` + `rememberNavBackStack` |
