package com.diegocunha.pokedex.feature.evolutions.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.diegocunha.pokedex.feature.evolutions.presentation.EvolutionScreen

fun EntryProviderBuilder<NavKey>.evolutionEntries() {
    entry<EvolutionDestination> { key ->
        EvolutionScreen(pokemonId = key.pokemonId)
    }
}
