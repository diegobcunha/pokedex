# SDD: Design System — Pokedex Theme

## Status
Complete

## Last Updated
2026-03-17

---

## Decisions Made

### 1. Dynamic color disabled by default
`dynamicColor = false` is the new default in `PokedexTheme`. Android 12+ dynamic color would override all brand tokens, breaking the Pokedex red identity. Callers can still opt in by passing `dynamicColor = true`.

### 2. No custom font — system sans-serif only
All 15 type roles use `FontFamily.Default`. This avoids bundling a custom typeface, keeps APK size small, and respects the user's system font preference. Can be revisited when brand guidelines require a specific typeface.

### 3. Spacing as a CompositionLocal, not an extension on Dp
`PokedexSpacing` is a plain `object` provided via `LocalPokedexSpacing` (a `staticCompositionLocalOf`). A `MaterialTheme.spacing` extension property provides ergonomic access at call sites. Using `staticCompositionLocalOf` (vs `compositionLocalOf`) means recomposition is not triggered on spacing changes — appropriate because spacing is constant at runtime.

### 4. No new spacing tokens for non-standard values
Values that do not map to a token (e.g. 12 dp, 6 dp, 80 dp image size) were either left as literals or expressed as token arithmetic (`spacing.sm + spacing.xs` = 12 dp). Introducing named tokens for one-off values would inflate the scale without adding clarity.

### 5. Shape `extraLarge = 50 dp` for pill chips
`TypeChip` previously used `RoundedCornerShape(50)` (percent-based). Replacing it with `MaterialTheme.shapes.extraLarge` (50 dp absolute) keeps the pill appearance while routing through the theme, making it overridable in tests and previews.

---

## Technical Features Implemented

### `core-ui` module — theme package

| File | Change |
|---|---|
| `Color.kt` | Full Pokedex palette: 12 light tokens + 11 dark tokens (PokedexRed, PokedexNavy, PokedexYellow and their variants) |
| `Type.kt` | All 15 Material3 type roles defined with Pokedex-appropriate sizes and weights |
| `Shape.kt` | New file — `PokedexShapes` with five radii: extraSmall 4 dp, small 8 dp, medium 16 dp, large 24 dp, extraLarge 50 dp |
| `Spacing.kt` | New file — `PokedexSpacing` object (xxs=2, xs=4, sm=8, md=16, lg=24, xl=32, xxl=48 dp), `LocalPokedexSpacing`, and `MaterialTheme.spacing` extension |
| `Theme.kt` | Pokedex light/dark `ColorScheme`, `dynamicColor=false` default, `CompositionLocalProvider` for spacing, `PokedexShapes` wired into `MaterialTheme` |

### `feature:pokemon` — composable migration

| File | Change |
|---|---|
| `TypeChip.kt` | Shape: `RoundedCornerShape(50)` → `MaterialTheme.shapes.extraLarge`; padding → `spacing.md` / `spacing.xs + 2.dp` |
| `StatBar.kt` | Label width: `48.dp` → `spacing.xxl`; spacer: `8.dp` → `spacing.sm` |
| `PokemonListScreen.kt` | Card padding, row padding, loading indicator padding → spacing tokens |
| `PokemonDetailScreen.kt` | Content padding, arrangement spacing, spacer height → spacing tokens |

---

## Verification

All checks passed after implementation:

- `:core-ui:compileDebugKotlin` — no errors
- `:feature:pokemon:compileDebugKotlin` — no errors
- `assembleDebug` — BUILD SUCCESSFUL
