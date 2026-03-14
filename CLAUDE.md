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
- **No DI framework, networking, or local DB yet** — these need to be added as features are built

## Key Files

- `app/src/main/java/Thicom/diegocunha/pokedex/MainActivity.kt` — Entry point
- `app/src/main/java/com/diegocunha/pokedex/ui/theme/` — Material3 theme (Color, Theme, Type)
- `gradle/libs.versions.toml` — Centralized version catalog for all dependencies
- `app/build.gradle.kts` — App module build configuration (Java 11, Compose enabled)

## Dependencies

All dependency versions are managed via the version catalog at `gradle/libs.versions.toml`. Add new dependencies there first, then reference them in `build.gradle.kts`.

## Theme

The app uses Material Design 3 with dynamic color support (Android 12+) and automatic light/dark mode. The theme is configured in `ui/theme/Theme.kt`.
