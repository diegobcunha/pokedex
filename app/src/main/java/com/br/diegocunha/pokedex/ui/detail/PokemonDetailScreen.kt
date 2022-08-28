package com.br.diegocunha.pokedex.ui.detail

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.br.diegocunha.pokedex.ui.navigation.PokemonParam

@Composable
fun PokemonDetailScreen(navController: NavController, params: PokemonParam) {
    Text(params.name)
}