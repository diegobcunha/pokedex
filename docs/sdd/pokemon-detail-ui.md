# Pokemon Detail Screen UI

## Status
Complete

## Last Updated
2026-03-15

---

## Decisions Made

### Type & Stat Colors in Feature Layer
Type colors and stat colors are defined inside `feature/pokemon` rather than `core-ui`. `core-ui/Color.kt` holds brand palette seeds for Material3; Pokemon type/stat colors are game-domain semantics and belong in the feature that uses them.

### PokemonType Enum for Color Mapping
A `PokemonType` enum in the `components/` package centralises all type-to-color mappings. Both `TypeChip` and the header background derive their color via `PokemonType.fromName(name)`, ensuring a single source of truth. String-comparison helper functions (`typeColor`, `headerColor`) were removed.

### TopAppBar Above Image via Column Layout
The TopAppBar and the image area are stacked in a `Column` that carries the type background color. This guarantees the TopAppBar always sits above the image — no overlay or z-ordering required. The transparent `containerColor` on the TopAppBar lets the column's background show through uniformly.

### Background Color Owned by the Screen, Not the Header Component
`PokemonHeader` is a stateless image-only composable (`imageUrl`, `name`). The background color is applied by `PokemonDetailContent` using `Modifier.background(typeColor)` on the wrapping `Column`. This keeps `PokemonHeader` reusable and avoids duplicating the color-resolution logic.

### Header Fixed, Content Scrolls
`verticalScroll` is applied only to the white content `Column` below the header. The header area does not scroll.

### No Domain Model Changes
Weight/height formatting (`÷ 10.0`) and ID zero-padding (`padStart(3, '0')`) are pure UI concerns applied in the Composable layer.

---

## Technical Features Implemented

### New Dependency
- **Coil 2.7.0** (`io.coil-kt:coil-compose`) — added to `gradle/libs.versions.toml` and `feature/pokemon/build.gradle.kts`

### New Components (`presentation/detail/components/`)

| File | Export | Responsibility |
|---|---|---|
| `PokemonType.kt` | `enum PokemonType` | 18 types + `UNKNOWN`, each with a `color: Color`; `fromName(name)` for lookup |
| `TypeChip.kt` | `TypeChip(type)` | Pill-shaped chip — background from `PokemonType.fromName(type).color`, white text |
| `StatBar.kt` | `StatBar(stat)` | Row: fixed-width label + `LinearProgressIndicator` (value/300) + "value/300" text |
| `PokemonHeader.kt` | `PokemonHeader(imageUrl, name)` | 150dp `Box` with centered `AsyncImage` (Coil); no background |

### Stat Abbreviation & Color Mapping (StatBar)

| API name | Label | Color |
|---|---|---|
| hp | HP | Red `#E53935` |
| attack | ATK | Orange `#FF9800` |
| defense | DEF | Blue `#1E88E5` |
| speed | SPD | Light Blue `#29B6F6` |
| special-attack | SATK | Green `#43A047` |
| special-defense | SDEF | Teal `#00897B` |

Stat max is **300** for all stats.

### Updated Files
- **`PokemonDetailScreen.kt`** — full designed layout replacing the placeholder; `onNavigateBack` parameter added; 3 `@Preview` functions included
- **`PokemonNavGraph.kt`** — threads `onNavigateBack` lambda through to the screen
- **`MainActivity.kt`** — passes `navController.popBackStack()` as `onNavigateBack`

### Screen Layout
```
Column (fillMaxSize)
 ├── Column (background = typeColor)
 │    ├── TopAppBar (transparent) ← back arrow | "Pokedex" | #006
 │    └── PokemonHeader           ← AsyncImage, 150dp, below TopAppBar
 └── Column (verticalScroll, padding 16dp)
      ├── Text(name, headlineLarge, bold)
      ├── Row { TypeChip per type }
      ├── Row { Weight column | Height column }
      ├── Text("Base Stats", titleMedium, bold)
      ├── Column { StatBar per stat }
      └── Button("View Evolutions")
```

---

## Data Shown (from existing `Pokemon` domain model)
- `id` → formatted as `#006` (zero-padded 3 digits)
- `name` → lowercased
- `types` → one `TypeChip` per type; first type determines header background color
- `weight` → `weight / 10.0` KG
- `height` → `height / 10.0` M
- `stats` → one `StatBar` per stat (hp, attack, defense, special-attack, special-defense, speed)
- `imageUrl` → loaded with Coil `AsyncImage`
