# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests on device/emulator
./gradlew lint                   # Run lint checks
./gradlew clean                  # Clean build artifacts
```

To run a single test class:
```bash
./gradlew test --tests "com.diegocunha.pokedex.ExampleUnitTest"
```

## Project Overview

Android Pokedex app with Pokemon list/detail features implemented. Architecture is multi-module clean architecture with MVI pattern, Paging 3, Room caching, and 100% Compose UI.

## Module Structure

```
:app                  — Application entry point, NavHost, Koin initialization
:core                 — MVI base classes, Resource sealed class, DispatchersProvider
:core-ui              — Material3 theme, shared Compose components, Spacing system
:datasource           — Retrofit API, Room database, Repository, DTOs, DAOs
:feature:pokemon      — Pokemon list + detail (fully implemented)
:feature:evolutions   — Evolution display (placeholder only)
```

## Architecture & Technology

- **UI:** 100% Jetpack Compose with Material Design 3, no XML layouts
- **Pattern:** MVI via `BaseViewModel<State, Intent>` in `:core`
- **Language:** Kotlin 2.2.10
- **Min SDK:** 24 (Android 7.0), Target/Compile SDK: 36
- **Package root:** `com.diegocunha.pokedex`
- **DI:** Koin 4.0.4 — modules loaded in `PokedexApplication`
- **Networking:** Retrofit 2.11.0 + OkHttp 4.12.0 + Kotlinx Serialization
- **Database:** Room 2.7.1 with 24-hour TTL caching strategy
- **Pagination:** Paging 3 with `RemoteMediator` pattern
- **Image loading:** Coil 2.7.0
- **Navigation:** AndroidX Navigation Compose 2.9.7

## Key Files

- `app/src/main/java/com/diegocunha/pokedex/MainActivity.kt` — Entry point with NavHost
- `app/src/main/java/com/diegocunha/pokedex/PokedexApplication.kt` — Koin setup
- `core/src/main/java/com/diegocunha/pokedex/core/mvi/BaseViewModel.kt` — MVI base
- `core/src/main/java/com/diegocunha/pokedex/core/Resource.kt` — Success/Error/Loading sealed class
- `datasource/src/main/java/com/diegocunha/pokedex/datasource/network/PokemonApiService.kt` — Retrofit API
- `datasource/src/main/java/com/diegocunha/pokedex/datasource/repository/PokemonRepositoryImpl.kt` — Caching repository
- `core-ui/src/main/java/com/diegocunha/pokedex/coreui/theme/` — Material3 theme
- `gradle/libs.versions.toml` — Centralized version catalog for all dependencies

## Feature: Pokemon (Implemented)

**List:** Paginated with Paging 3 + `PokemonRemoteMediator`. Shows name, image, type color.
**Detail:** Full view with image, height/weight, type chips, stat bars, abilities. 24-hour TTL cache.
**Navigation routes:** `feature/pokemon/navigation/PokemonRoutes.kt`
**Domain models:** `feature/pokemon/domain/model/` (Pokemon, PokemonEntry, PokemonStat)
**Mappers:** `feature/pokemon/domain/mapper/PokemonMapper.kt`

## Feature: Evolutions (Placeholder)

Evolution screen renders `"Evolution for: {pokemonId}"`. API service has the endpoint. Not yet implemented.

## Data Layer

**API endpoints** (PokeAPI):
- `GET /pokemon?limit={limit}&offset={offset}` — paginated list
- `GET /pokemon/{id}` — detail by ID
- `GET /evolution-chain/{id}` — evolution (unused)

**Room entities:** `PokemonListEntryEntity`, `PokemonDetailEntity` (with `lastFetched` TTL), `RemoteKeyEntity`

**DTOs live exclusively in `:datasource`** — never leak into ViewModels or Composables.

## Dependencies

All dependency versions are managed via the version catalog at `gradle/libs.versions.toml`. Add new dependencies there first, then reference them in `build.gradle.kts`.

## Unit Testing

- **Mocking:** MockK
- **Async:** Kotlin Coroutines Test
- **Flow testing:** Turbine
- **Coverage:** kotlinx-kover
  - Compose views and Compose screens are excluded from coverage
  - Minimum coverage threshold: **80%**

## Theme

Material Design 3 with dynamic color support (Android 12+) and automatic light/dark mode. Theme in `core-ui/src/main/java/com/diegocunha/pokedex/coreui/theme/`. Custom `Spacing` via `CompositionLocal`. 18 Pokemon types with mapped colors in `feature/pokemon/presentation/common/PokemonType.kt`.

## Architecture Rules

**Clean Architecture DTO rule:** DTOs from `:datasource` must never appear in ViewModels or Composables. Each feature owns its domain models in `feature/<name>/domain/model/` and mappers in `feature/<name>/domain/mapper/`. PagingSources live in `feature/<name>/data/paging/` and map DTOs to domain models inside `load()`. For non-paging repository calls, the ViewModel applies the mapper directly after receiving the `Resource`.

**UseCase rule:** Create a UseCase only when: (1) non-trivial business logic exists beyond fetching and mapping, (2) multiple repositories are orchestrated, or (3) logic is shared across two or more ViewModels. Direct repository calls from the ViewModel are the default.

## Development Methodology

This project uses **SDD (Specification-Driven Development)**. All feature work follows these phases:

1. **Interview** — Ask the user about functional, technical, and documentation requirements before any implementation
2. **Specification** — Enter plan mode, present the full implementation plan for review before any code is written
3. **Implementation** — Execute step by step, phase-gated, only after the user approves the plan

**Rules:**
- Never advance to the next phase until the user explicitly says the current phase is ready
- The user may return to any previous phase at any time (flow possibility, not a phase). When this happens, analyze the impact and replan forward from there
- When development is finished, create a SDD documentation file for the feature in the project containing:
  - Decisions made
  - Technical features implemented
  - Current status of the feature
  - Last updated date
