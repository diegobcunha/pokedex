# Pokemon Evolution Feature

## Status
Complete

## Last Updated
2026-04-11

---

## Decisions Made

### Evolution Data Inline on Detail Screen
The evolution chain is displayed directly on the Pokemon detail screen rather than on a separate screen. The pre-existing "View Evolutions" button and the `NavigateToEvolution` intent/effect were removed. The `:feature:evolutions` module skeleton is retained but no longer navigated to from the detail screen.

### Minimum Network Calls Strategy (2 on First Load, 0 on Cache Hit)
Getting from a Pokemon ID to an evolution chain requires two sequential API calls:
1. `GET /pokemon-species/{pokemonId}` — returns the evolution chain URL
2. `GET /evolution-chain/{chainId}` — returns the full chain

The chain ID is extracted from the species response URL (e.g. `.../evolution-chain/1/` → `1`). On subsequent loads the Room cache is hit immediately and no network calls are made.

### Evolution Cache Has No TTL
Evolution chains are permanent game data that never change. Unlike `PokemonDetailEntity` (which has a 24-hour TTL), `PokemonEvolutionEntity` has no expiry — once stored, it is returned forever without a network refresh.

### Pokemon Images Reuse Existing Sprite Pattern
Each `ChainLink` in the PokeAPI response contains a species URL (e.g. `.../pokemon-species/25/`) from which the numeric ID is extracted. The sprite URL is constructed as `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/{id}.png`, which is the same pattern used by the rest of the app. No extra API calls per evolution node are needed.

### Full-Screen Loading Until Both Detail + Evolution Are Ready
The `PokemonDetailViewModel` uses `combine` on `getPokemonDetail` and `getEvolutionData` flows. The screen stays in `Loading` state until both emit `Success`. If either emits `Error`, the screen transitions to `Error` state (with retry). No partial/skeleton states are shown.

### Vertical Layout with Branching Side-by-Side
The evolution chain renders top-to-bottom. Linear chains show each stage stacked vertically with an arrow and trigger label between stages. Branching evolutions (e.g. Eevee's eeveelutions) show all branches in a `Row` at the same vertical level, each with its own arrow and trigger label. Tapping any evolution node navigates to that Pokemon's detail screen.

### No-Evolution Message
For Pokemon with no evolvesTo entries (e.g. Mewtwo), the base Pokemon node is shown followed by the text "This Pokémon does not evolve."

### Database Version Bump with Destructive Migration
Adding `PokemonEvolutionEntity` required bumping the Room database version from 1 to 2. `fallbackToDestructiveMigration(true)` is used since this is a development-stage project with no production data to preserve.

### Trigger Label Priority
When an `EvolutionDetail` object contains multiple conditions, the trigger label shown to the user follows this priority order:
1. `minLevel` → "Level {n}"
2. `item` → "Use {Item Name}"
3. `heldItem` → "Hold {Item Name}"
4. `minHappiness` → "High Friendship"
5. `minBeauty` → "High Beauty"
6. `minAffection` → "High Affection"
7. `timeOfDay` ("day"/"night") → "Daytime" / "Nighttime"
8. `knownMove` → "Know {Move Name}"
9. `needsOverworldRain` → "Rain"
10. `turnUpsideDown` → "Turn Upside Down"
11. Fallback → formatted trigger name (e.g. "level-up" → "Level Up")

---

## Technical Features Implemented

### New Files

| File | Description |
|---|---|
| `datasource/.../model/PokemonSpeciesResponse.kt` | DTO for `GET /pokemon-species/{id}` — contains evolution chain URL |
| `datasource/.../db/entity/PokemonEvolutionEntity.kt` | Room entity `pokemon_evolution` — keyed by `pokemonId`, stores `chainJson` + `chainId`, no TTL |
| `datasource/.../db/dao/PokemonEvolutionDao.kt` | `getByPokemonId` + `insert` |
| `feature/pokemon/.../domain/model/EvolutionChain.kt` | Domain models: `EvolutionChain(base: EvolutionNode)` and `EvolutionNode` (recursive tree) |
| `feature/pokemon/.../domain/mapper/EvolutionMapper.kt` | `EvolutionChainResponse.toDomain()` — walks tree recursively, extracts IDs from URLs, builds trigger labels |
| `feature/pokemon/.../presentation/detail/components/EvolutionSection.kt` | Composable for evolution chain: `EvolutionSection`, `EvolutionNodeTree`, `EvolutionNodeItem`, `EvolutionArrow` |
| `feature/pokemon/src/test/.../mapper/EvolutionMapperTest.kt` | Unit tests for mapper covering single-stage, linear chain, branching, and all trigger label priorities |

### Modified Files

| File | Change |
|---|---|
| `datasource/.../model/EvolutionChainResponse.kt` | Added `evolutionDetails: List<EvolutionDetail>` to `ChainLink`; added `EvolutionDetail` data class |
| `datasource/.../network/PokemonApiService.kt` | Added `getPokemonSpecies(id)` endpoint |
| `datasource/.../db/PokedexDatabase.kt` | Version 2, added `PokemonEvolutionEntity` and `pokemonEvolutionDao()` |
| `datasource/.../repository/PokemonRepository.kt` | Added `getEvolutionData(pokemonId: Int): Flow<Resource<EvolutionChainResponse>>` |
| `datasource/.../repository/PokemonRepositoryImpl.kt` | Added `pokemonEvolutionDao` param; implemented cache-first `getEvolutionData`; added `extractIdFromUrl` helper |
| `datasource/.../di/DatasourceModule.kt` | Registered `pokemonEvolutionDao`; added `fallbackToDestructiveMigration(true)`; updated repository binding |
| `feature/pokemon/.../presentation/detail/PokemonDetailState.kt` | `Success` now holds `evolution: EvolutionChain`; replaced `NavigateToEvolution` with `NavigateToPokemon(pokemonId)` |
| `feature/pokemon/.../presentation/detail/PokemonDetailViewModel.kt` | `loadDetail` uses `combine` on both flows; replaced `NavigateToEvolution` intent with `NavigateToPokemon` |
| `feature/pokemon/.../presentation/detail/PokemonDetailScreen.kt` | Replaced "View Evolutions" button with `EvolutionSection`; added `HorizontalDivider`; updated previews |
| `feature/pokemon/.../navigation/PokemonNavGraph.kt` | Replaced `onNavigateToEvolution` param with `onNavigateToPokemon` |
| `app/.../MainActivity.kt` | Updated `pokemonGraph` call to use `onNavigateToPokemon` navigating to detail route |
| `datasource/src/test/.../PokemonRepositoryImplTest.kt` | Fixed constructor; added 4 evolution cache/network/error tests |
| `feature/pokemon/src/test/.../PokemonDetailViewModelTest.kt` | Fixed mocks to include `getEvolutionData`; replaced `NavigateToEvolution` test; added evolution-error test |

### Architecture

```
PokemonDetailScreen
  └── PokemonDetailViewModel (combine detail + evolution flows)
        ├── repository.getPokemonDetail(id)   → Flow<Resource<PokemonResponse>>
        └── repository.getEvolutionData(id)   → Flow<Resource<EvolutionChainResponse>>
              ├── Cache hit  → Room pokemon_evolution table (pokemonId key, no TTL)
              └── Cache miss → getPokemonSpecies(id) → getEvolutionChain(chainId) → Room insert

PokemonDetailState.Success(pokemon, evolution)
  └── evolution: EvolutionChain
        └── base: EvolutionNode (recursive tree with pokemonId, name, imageUrl, trigger, evolvesTo)
```

### Screen Layout (evolution section)
```
PokemonDetailContent (scrollable column)
  ├── ...existing sections (types, weight/height, base stats)...
  ├── HorizontalDivider
  └── EvolutionSection
        ├── "Evolution Chain" (titleMedium, bold)
        └── EvolutionNodeTree (recursive)
              ├── EvolutionNodeItem  ← clickable: image (80dp) + name
              ├── EvolutionArrow     ← ▼ + trigger label
              ├── [for linear]  EvolutionNodeTree (child)
              └── [for branching] Row { EvolutionNodeTree per branch }
```
