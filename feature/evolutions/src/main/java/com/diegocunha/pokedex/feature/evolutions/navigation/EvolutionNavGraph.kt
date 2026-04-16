package com.diegocunha.pokedex.feature.evolutions.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.diegocunha.pokedex.feature.evolutions.presentation.EvolutionScreen

@Composable
fun EntryProviderScope<NavKey>.EvolutionEntries() {
    entry<EvolutionDestination> { key ->
        EvolutionScreen(pokemonId = key.pokemonId)
    }
}
