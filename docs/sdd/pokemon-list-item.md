# SDD: Pokemon List Item

## Feature: pokemon-list-view — PokemonListItem composable

---

## Decisions Made

### Display format
- Pokemon name and ID are shown as `#<id> <name>` (e.g., `#1 Bulbasaur`).
- ID is used as-is, with no zero-padding.
- Name is capitalized via `replaceFirstChar { it.uppercase() }` — locale-safe and idempotent on already-capitalized names.

### Composable extraction
- `PokemonListItem` is extracted as a private composable in `PokemonListScreen.kt`.
- No separate file was created — the component is scoped to the list screen and not shared elsewhere.
- Existing typography and click handling were preserved unchanged.

### Previewability of `PokemonListScreen`
- `PokemonListScreen` originally accepted a `ViewModel` directly, making it impossible to preview.
- A private stateless `PokemonListScreenContent` composable was extracted, accepting `LazyPagingItems`, `SnackbarHostState`, and callbacks.
- `PokemonListScreen` delegates to it; this is the only structural change to the screen.
- Three previews cover the three distinct UI states: list, loading, and error+retry.

### No unit tests
- Compose views are excluded from coverage per project rules. Preview-only verification was agreed upon.

---

## Technical Features Implemented

| File | Change |
|------|--------|
| `PokemonListScreen.kt` | Extracted `PokemonListItem` composable with `#id name` format and capitalization |
| `PokemonListScreen.kt` | Extracted `PokemonListScreenContent` stateless composable to enable screen-level previews |
| `PokemonListScreen.kt` | Added `@Preview` for `PokemonListItem` (single item) |
| `PokemonListScreen.kt` | Added `@Preview` for `PokemonListScreenContent` in List, Loading, and Error states |

---

## Current Status

**Complete.** The list item component is in place:
- Each row displays `#<id> <name>` with the name's first letter capitalized
- `PokemonListItem` is a named, previewed composable
- All three screen states (list, loading, error) have dedicated previews
- Build passes with no warnings

---

## Last Updated

2026-03-15
