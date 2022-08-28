package com.br.diegocunha.pokedex.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.br.diegocunha.pokedex.ui.detail.PokemonDetailScreen
import com.br.diegocunha.pokedex.ui.home.HomeScreen
import com.br.diegocunha.pokedex.ui.navigation.PokeScreen
import com.br.diegocunha.pokedex.ui.navigation.PokemonParamType
import com.br.diegocunha.pokedex.ui.theme.PokeDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeDexTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = PokeScreen.Home.route
                ) {
                    composable(PokeScreen.Home.route) {
                        HomeScreen(navController = navController)
                    }

                    composable(
                        PokeScreen.PokemonDetail.route,
                        arguments = listOf(navArgument(PokeScreen.PokemonDetail.argumentName) {
                            type = PokemonParamType()
                        })
                    ) { navBackStackEntry ->
                        PokemonDetailScreen(
                            navController,
                            navBackStackEntry.arguments?.getParcelable(
                                PokeScreen.PokemonDetail.argumentName
                            )
                                ?: throw Exception("Argument should be passed")
                        )
                    }
                }

            }
        }
    }
}