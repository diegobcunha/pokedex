package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegocunha.pokedex.feature.pokemon.domain.model.Pokemon
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PokemonDetailScreen(
    viewModel: PokemonDetailViewModel,
    onNavigateToEvolution: (pokemonId: String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is PokemonDetailEffect.NavigateToEvolution -> onNavigateToEvolution(effect.pokemonId)
            }
        }
    }

    when (val currentState = state) {
        is PokemonDetailState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PokemonDetailState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = { viewModel.sendIntent(PokemonDetailIntent.Retry) }) {
                    Text(text = "Retry")
                }
            }
        }

        is PokemonDetailState.Success -> {
            PokemonDetailContent(
                pokemon = currentState.pokemon,
                onEvolutionClick = { viewModel.sendIntent(PokemonDetailIntent.NavigateToEvolution) }
            )
        }
    }
}

@Composable
private fun PokemonDetailContent(
    pokemon: Pokemon,
    onEvolutionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = pokemon.name.replaceFirstChar { it.uppercase() }, fontSize = 28.sp, fontWeight = FontWeight.Bold)

        pokemon.imageUrl?.let { url ->
            Text(text = "Image: $url")
        }

        HorizontalDivider()

        SectionLabel("Types")
        Text(text = pokemon.types.joinToString(" · "))

        HorizontalDivider()

        SectionLabel("Stats")
        pokemon.stats.forEach { stat ->
            Text(
                text = "${stat.name}: ${stat.value}",
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalDivider()

        SectionLabel("Abilities")
        Text(text = pokemon.abilities.joinToString(", "))

        Button(
            onClick = onEvolutionClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "View Evolutions")
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
}
