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

This is a Android Pokedex app in early development. The current codebase is minimal — only theme setup and a placeholder `Greeting` composable exist. The project is ready for feature development.

## Architecture & Technology

- **UI:** 100% Jetpack Compose with Material Design 3, no XML layouts
- **Language:** Kotlin 2.2.10
- **Min SDK:** 24 (Android 7.0), Target/Compile SDK: 36
- **Module structure:** Single `:app` module
- **Package root:** `com.diegocunha.pokedex`
- **DI framework:** Insert-Koin
- **Networking:** Retrofit + OkHttp to network call
- **No local DB yet** — to be added as features require it

## Key Files

- `app/src/main/java/com/diegocunha/pokedex/MainActivity.kt` — Entry point
- `app/src/main/java/com/diegocunha/pokedex/ui/theme/` — Material3 theme (Color, Theme, Type)
- `gradle/libs.versions.toml` — Centralized version catalog for all dependencies
- `app/build.gradle.kts` — App module build configuration (Java 11, Compose enabled)

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

The app uses Material Design 3 with dynamic color support (Android 12+) and automatic light/dark mode. The theme is configured in `ui/theme/Theme.kt`.

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
