package com.diegocunha.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.diegocunha.pokedex.coreui.theme.PokedexTheme
import com.diegocunha.pokedex.feature.evolutions.navigation.evolutionGraph
import com.diegocunha.pokedex.feature.pokemon.navigation.PokemonRoutes
import com.diegocunha.pokedex.feature.pokemon.navigation.pokemonGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = PokemonRoutes.LIST
                ) {
                    pokemonGraph(
                        onNavigateToDetail = { id -> navController.navigate(PokemonRoutes.detail(id)) },
                        onNavigateToPokemon = { id -> navController.navigate(PokemonRoutes.detail(id)) },
                        onNavigateBack = { navController.popBackStack() }
                    )
                    evolutionGraph()
                }
            }
        }
    }
}
