package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PokemonDetailScreen(
    pokemonId: String,
    onNavigateToEvolution: (pokemonId: String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pokemon Detail: $pokemonId")
        Button(onClick = { onNavigateToEvolution(pokemonId) }) {
            Text(text = "Go to Evolution")
        }
    }
}
