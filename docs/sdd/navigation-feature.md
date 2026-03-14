# SDD: Navigation Feature

## Decisions Made

- **Navigation strategy:** Option A — `NavGraphBuilder` extensions per feature module. Each feature module owns its own routes and graph wiring, keeping navigation logic colocated with the feature.
- **Library:** Jetpack Compose Navigation (`androidx.navigation:navigation-compose:2.9.7`)
- **Dependency ownership:** `navigation-compose` is declared once in `core-ui` as `api()`, exposing it transitively to all feature modules and `app` via their existing `:core-ui` dependency. No module declares it directly.
- **Route format:** String-based routes with path parameters (e.g., `pokemon/detail/{pokemonId}`), using `navArgument` for type-safe argument extraction.
- **Argument type:** `pokemonId` passed as `NavType.StringType` between all three screens.

## Technical Features Implemented

- `gradle/libs.versions.toml` — Added `navigation = "2.9.7"` version and `androidx-navigation-compose` library entry.
- `core-ui/build.gradle.kts` — Added `navigation-compose` as `api()`, making it available transitively to all consumers.
- `feature/pokemon/.../navigation/PokemonRoutes.kt` — Route constants and URL builder for list and detail.
- `feature/pokemon/.../navigation/PokemonNavGraph.kt` — `NavGraphBuilder.pokemonGraph()` extension wiring list and detail composables.
- `feature/pokemon/.../presentation/list/PokemonListScreen.kt` — Placeholder list screen with navigation trigger.
- `feature/pokemon/.../presentation/detail/PokemonDetailScreen.kt` — Placeholder detail screen displaying `pokemonId` with navigation trigger.
- `feature/evolutions/.../navigation/EvolutionRoutes.kt` — Route constants and URL builder for evolution.
- `feature/evolutions/.../navigation/EvolutionNavGraph.kt` — `NavGraphBuilder.evolutionGraph()` extension wiring the evolution composable.
- `feature/evolutions/.../presentation/EvolutionScreen.kt` — Placeholder evolution screen displaying `pokemonId`.
- `app/.../MainActivity.kt` — Updated to host `NavHost` with `rememberNavController`, wiring both feature graphs.

## Navigation Flow

```
PokemonListScreen → PokemonDetailScreen → EvolutionScreen
     (pokemonId passed via route arguments between each screen)
```

## Current Status

Navigation scaffold is complete with placeholder screens. All routes are wired and back navigation works via the default back stack. Ready for real feature implementation on each screen.

## Last Updated

2026-03-14 — Updated navigation version to 2.9.7 and moved dependency ownership to `core-ui`
