package com.br.diegocunha.pokedex.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.navigation.navArgument
import com.br.diegocunha.pokedex.extensions.parcelableData
import com.br.diegocunha.pokedex.ui.detail.PokemonDetailScreen
import com.br.diegocunha.pokedex.ui.home.HomeScreen
import com.br.diegocunha.pokedex.ui.navigation.PokeScreen
import com.br.diegocunha.pokedex.ui.navigation.PokemonParamType
import com.br.diegocunha.pokedex.ui.theme.PokeDexTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeDexTheme {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(
                    navController = navController,
                    startDestination = PokeScreen.Home.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    }
                ) {
                    composable(PokeScreen.Home.route) {
                        HomeScreen(navController = navController)
                    }

                    composable(
                        route = PokeScreen.PokemonDetail.route,
                        arguments = listOf(navArgument(PokeScreen.PokemonDetail.argumentName) {
                            type = PokemonParamType()
                        })
                    ) { navBackStackEntry ->
                        PokemonDetailScreen(
                            navController,
                            navBackStackEntry.arguments?.parcelableData(PokeScreen.PokemonDetail.argumentName)
                                ?: throw Exception("Argument should be passed")
                        )
                    }
                }
            }
        }
    }
}