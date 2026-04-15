package com.diegocunha.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.diegocunha.pokedex.coreui.theme.PokedexTheme
import com.diegocunha.pokedex.feature.evolutions.navigation.evolutionEntries
import com.diegocunha.pokedex.feature.pokemon.navigation.PokemonDetail
import com.diegocunha.pokedex.feature.pokemon.navigation.PokemonList
import com.diegocunha.pokedex.feature.pokemon.navigation.pokemonEntries

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                val backStack = rememberNavBackStack(PokemonList)
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        pokemonEntries(
                            onNavigateToDetail = { id -> backStack.add(PokemonDetail(id)) },
                            onNavigateToPokemon = { id -> backStack.add(PokemonDetail(id)) },
                            onNavigateBack = { backStack.removeLastOrNull() }
                        )
                        evolutionEntries()
                    }
                )
            }
        }
    }
}
