# Pokedex — Android Portfolio App

![Coverage](https://img.shields.io/badge/coverage-95%25-brightgreen)

A modern Android application built as a portfolio project, showcasing production-grade architecture, clean code practices, and a structured AI-assisted development workflow using **Specification-Driven Development (SDD)**.

---

## Purpose

This project was built to demonstrate senior-level Android engineering skills, including:

- Scalable multi-module architecture
- Clean separation of concerns using MVI
- Thoughtful dependency management and testability
- A disciplined, AI-augmented development process

The goal is not just a functional app, but a reference for how to approach Android development methodically and professionally.

---

## Architecture

### Multi-Module Structure

The app is split into focused, independently maintainable modules:

```
app/               → Thin shell: Koin initialization, NavHost, entry point
core/              → MVI base classes, shared business logic contracts
core-ui/           → Material3 theme, design system, navigation dependency
datasource/        → Network infrastructure (Retrofit, OkHttp, interceptors)
feature/
  ├── pokemon/     → Pokemon list and detail screens
  └── evolutions/  → Evolution detail screens
docs/sdd/          → Specification-Driven Development documentation
```

Each feature module owns its routes, navigation graph, DI module, and presentation layer. The `app` module acts solely as an integration point, keeping it free from business logic.

### MVI (Model-View-Intent)

State management follows a strict MVI pattern, implemented in `:core`:

- **State** is emitted as `StateFlow` — always observable, always has a current value
- **Intents** are sent through a `Channel` — one-shot, backpressure-safe events
- `BaseViewModel` exposes `sendIntent()` and `updateState()`, enforcing a unidirectional data flow

```
UI → Intent → ViewModel.processIntent() → updateState() → UI renders new State
```

### Navigation

Navigation uses Jetpack Compose Navigation with a **NavGraphBuilder extension pattern**: each feature module declares its own `NavGraph` extension, keeping routing ownership close to the feature. The `app` module composes them without knowing their internals.

```
PokemonListScreen → PokemonDetailScreen → EvolutionScreen
```

Routes are string constants with path parameters (e.g., `pokemon/detail/{pokemonId}`).

### Dependency Injection

Koin is used for DI throughout. Each module declares its own Koin module, and `PokedexApplication` aggregates all of them at startup. This keeps modules decoupled and testable in isolation.

---

## Tech Stack

| Layer | Library | Version |
|---|---|---|
| Language | Kotlin | 2.2.10 |
| UI | Jetpack Compose + Material3 | BOM 2024.09.00 |
| Navigation | Navigation Compose | 2.9.7 |
| Architecture | ViewModel + StateFlow | Lifecycle 2.8.7 |
| DI | Koin | 4.0.4 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| Serialization | kotlinx.serialization | 1.7.3 |
| Pagination | Androidx Paging 3 | 3.3.6 |
| Image Loading | Coil | 2.7.0 |
| Async | Kotlin Coroutines | 1.9.0 |
| Testing | MockK + Turbine + Coroutines Test | 1.13.13 / 1.2.0 |
| Coverage | kotlinx-kover | 0.9.1 |

All versions are centralized in `gradle/libs.versions.toml`.

---

## Testing Strategy

- **Unit tests** use MockK for mocking and Turbine for Flow assertions
- **Async testing** uses `kotlinx-coroutines-test` with `UnconfinedTestDispatcher`
- **Coverage** is enforced via Kover with an **80% minimum threshold**
- Compose UI and screens are excluded from coverage (UI logic lives in the Composable layer, tested via instrumentation)

---

## Data Source

The app consumes the public [PokéAPI](https://pokeapi.co/) — a free, open REST API for Pokémon data. Network calls are made via Retrofit with a `HeaderInterceptor` for consistent request headers and an `HttpLoggingInterceptor` (debug builds only).

---

## Specification-Driven Development (SDD)

This project applies **SDD** as its development methodology, with AI (Claude) as a collaborative engineering assistant. SDD enforces three sequential, gated phases for every feature:

### Phase 1 — Interview
Before any code is written, requirements are gathered through structured questions covering:
- Functional behavior
- Technical constraints and edge cases
- Documentation needs

### Phase 2 — Specification
The full implementation plan is presented for review **before implementation begins**:
- Module boundaries and file structure
- Architectural decisions and trade-offs
- Test strategy

No code is written until the plan is explicitly approved.

### Phase 3 — Implementation
Development proceeds step-by-step, exactly as specified. Changes to scope require returning to Phase 2 to replan.

### Why SDD?

| Benefit | Explanation |
|---|---|
| Avoids rework | Problems are caught in planning, not after coding |
| Forces clarity | Vague requirements surface during the interview phase |
| Creates a paper trail | Every feature has a spec doc in `docs/sdd/` |
| Scales with AI assistance | AI can write more accurate code when given a precise spec |

SDD documentation for each feature lives in `docs/sdd/` and includes the decisions made, what was implemented, current status, and the last updated date.

---

## AI-Assisted Development

Claude (Anthropic) was used as a collaborative engineering partner throughout this project. The workflow treats AI not as an autocomplete tool, but as a **senior pair programmer** operating under the same constraints as a human engineer:

- Must follow SDD phases — no skipping ahead
- Must read code before modifying it
- Must propose, not execute, for risky changes
- Must produce clean, minimal, testable code

This approach demonstrates how AI can augment a senior developer's workflow without replacing engineering judgment — making it faster to deliver well-structured, maintainable code.

---

## Build & Run

```bash
# Clone and open in Android Studio, then:
./gradlew assembleDebug          # Build debug APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew lint                   # Run lint
./gradlew koverHtmlReport        # Generate test coverage report
```

**Requirements:**
- Android Studio Ladybug or newer
- JDK 11+
- Min Android: API 24 (Android 7.0)

---

## Features

### Pokemon List
- Paginated list of all Pokémon fetched from PokéAPI (20 per page, infinite scroll via Paging 3)
- Each entry shows the Pokémon number and name
- Loading, error (with retry), and success states

### Pokemon Detail
- Full detail screen with header color derived from the Pokémon's primary type
- Pokémon image loaded via Coil
- Type chips (color-coded for all 18 types)
- Physical stats: height and weight
- Base stats with animated progress bars (HP, Attack, Defense, Sp. Atk, Sp. Def, Speed)
- Abilities list
- "View Evolutions" button navigating to the evolution screen

### Evolutions
- Route scaffold in place; full implementation pending

---

## Project Status

| Module | Status |
|---|---|
| `:core` | Complete — MVI base classes, Resource wrapper, DispatchersProvider |
| `:core-ui` | Complete — Material3 theme, dynamic color (Android 12+) |
| `:datasource` | Complete — Retrofit/OkHttp, HeaderInterceptor, SafeApiCall, PokemonRepository |
| `:feature:pokemon` | Complete — list (pagination) + detail (types, stats, abilities, image) + full unit tests |
| `:feature:evolutions` | Scaffold only — route and placeholder screen in place, feature pending |

---

## About

Built by **Diego Cunha**, a senior Android developer with experience in production-grade Kotlin, Jetpack Compose, and scalable mobile architecture.

This project is intentionally transparent about its methodology — including AI usage — because the future of senior engineering is knowing how to leverage the right tools effectively, not pretending they don't exist.
