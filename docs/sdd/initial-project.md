# SDD Documentation — `initial-project`

**Last Updated:** 2026-03-14
**Status:** Completed

---

## Decisions Made

| Decision | Rationale |
|---|---|
| Multi-module architecture | Separation of responsibilities; each module owns its domain |
| `:core` handles MVI base classes | Shared ViewModel contract across all feature modules |
| `:core-ui` owns the theme and Compose design system | Visual components are independent from business logic |
| `:datasource` owns Retrofit/OkHttp setup | Network configuration is centralized and reusable; feature modules only declare their service interface when needed |
| Feature modules declare their own Koin modules | Encapsulation; `:app` only aggregates them |
| MVI with `StateFlow` + `Channel` | Unidirectional data flow; `StateFlow` for UI state, `Channel` for intents consumed in `BaseViewModel` |
| kotlinx.serialization | Official Kotlin serialization; integrates cleanly with Retrofit converter |
| Navigation deferred | Navigation strategy depends on feature UX which is not yet defined |
| Feature modules contain no business logic at this stage | Domain models, repositories, use cases, and network calls belong to each feature's own development phase |
| kover excludes `presentation` packages and Compose annotated classes | Compose screens are not unit-testable meaningfully; coverage metric should reflect business logic only |

---

## Technical Features Implemented

### `:core`
- `MviState` and `MviIntent` marker interfaces
- `BaseViewModel<State, Intent>` — abstract ViewModel with `StateFlow<State>`, `Channel<Intent>`, `sendIntent()`, `updateState()`, and `processIntent()`
- `CoreModule` — empty Koin module placeholder

### `:core-ui`
- Material Design 3 theme (`PokedexTheme`) with dynamic color (Android 12+) and dark/light mode
- `Color.kt`, `Type.kt`, `Theme.kt` migrated from `:app`
- `CoreUiModule` — empty Koin module placeholder

### `:datasource`
- `NetworkConfig` — `BASE_URL` for PokeAPI
- `HeaderInterceptor` — adds `Accept` and `Content-Type` headers
- `OkHttpClientFactory` — builds `OkHttpClient` with `HttpLoggingInterceptor` in debug
- `RetrofitFactory` — builds `Retrofit` with kotlinx.serialization converter
- `DatasourceModule` — Koin bindings for `OkHttpClient`, `Json`, and `Retrofit`

### `:feature:pokemon`
- Module scaffolding: `build.gradle.kts`, `AndroidManifest.xml`
- `PokemonModule` — empty Koin module placeholder

### `:feature:evolutions`
- Module scaffolding: `build.gradle.kts`, `AndroidManifest.xml`
- `EvolutionsModule` — empty Koin module placeholder

### `:app`
- `PokedexApplication` — initializes Koin with all modules
- `MainActivity` — entry point with `PokedexTheme`, placeholder content
- `AndroidManifest` updated with `android:name=".PokedexApplication"`

### Build & Coverage
- `gradle/libs.versions.toml` — all dependencies and plugins added
- `settings.gradle.kts` — all 6 modules declared
- Root `build.gradle.kts` — kover aggregation with 80% threshold, Compose packages excluded

---

## Module Dependency Graph

```
:app
  ├── :core
  ├── :core-ui
  ├── :datasource
  ├── :feature:pokemon
  │     ├── :core
  │     ├── :core-ui
  │     └── :datasource
  └── :feature:evolutions
        ├── :core
        ├── :core-ui
        └── :datasource
```

---

## Current Status

| Module | Status |
|---|---|
| `:core` | Complete — MVI base classes ready |
| `:core-ui` | Complete — theme ready, shared components to be added per feature |
| `:datasource` | Complete — network infrastructure ready |
| `:feature:pokemon` | Scaffolded — awaiting feature development |
| `:feature:evolutions` | Scaffolded — awaiting feature development |
| `:app` | Complete — thin shell wiring all modules |
