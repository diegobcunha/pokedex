package com.diegocunha.pokedex.feature.pokemon.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diegocunha.pokedex.coreui.theme.PokedexTheme
import com.diegocunha.pokedex.coreui.theme.spacing
import com.diegocunha.pokedex.feature.pokemon.domain.model.EvolutionChain
import com.diegocunha.pokedex.feature.pokemon.domain.model.EvolutionNode
import com.diegocunha.pokedex.feature.pokemon.domain.model.Pokemon
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonStat
import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.components.EvolutionSection
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.components.PokemonHeader
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.components.StatBar
import com.diegocunha.pokedex.feature.pokemon.presentation.detail.components.TypeChip
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PokemonDetailScreen(
    viewModel: PokemonDetailViewModel,
    onNavigateToPokemon: (pokemonId: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is PokemonDetailEffect.NavigateToPokemon -> onNavigateToPokemon(effect.pokemonId)
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
                evolution = currentState.evolution,
                onNavigateBack = onNavigateBack,
                onPokemonClick = { pokemonId ->
                    viewModel.sendIntent(PokemonDetailIntent.NavigateToPokemon(pokemonId))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokemonDetailContent(
    pokemon: Pokemon,
    evolution: EvolutionChain,
    onNavigateBack: () -> Unit,
    onPokemonClick: (pokemonId: String) -> Unit
) {
    val typeColor = PokemonType.fromName(pokemon.types.firstOrNull().orEmpty()).color
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(typeColor)
        ) {
            TopAppBar(
                title = { Text(text = "Pokedex") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Text(
                        text = "#${pokemon.id.padStart(3, '0')}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.md)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
            PokemonHeader(imageUrl = pokemon.imageUrl, name = pokemon.name)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm + MaterialTheme.spacing.xs)
        ) {
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                pokemon.types.forEach { type ->
                    TypeChip(type = type)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f KG".format(pokemon.weight / 10.0),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Weight",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f M".format(pokemon.height / 10.0),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Height",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Base Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                pokemon.stats.forEach { stat ->
                    StatBar(stat = stat)
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            EvolutionSection(
                evolution = evolution,
                onPokemonClick = onPokemonClick
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        }
    }
}

private val previewPokemon = Pokemon(
    id = "6",
    name = "charizard",
    height = 17,
    weight = 905,
    types = persistentListOf("fire", "flying"),
    stats = persistentListOf(
        PokemonStat("hp", 78),
        PokemonStat("attack", 84),
        PokemonStat("defense", 78),
        PokemonStat("special-attack", 109),
        PokemonStat("special-defense", 85),
        PokemonStat("speed", 100)
    ),
    imageUrl = null,
    abilities = persistentListOf("blaze", "solar-power")
)

private val previewEvolution = EvolutionChain(
    base = EvolutionNode(
        pokemonId = "4",
        pokemonName = "charmander",
        imageUrl = "",
        trigger = null,
        evolvesTo = listOf(
            EvolutionNode(
                pokemonId = "5",
                pokemonName = "charmeleon",
                imageUrl = "",
                trigger = "Level 16",
                evolvesTo = listOf(
                    EvolutionNode(
                        pokemonId = "6",
                        pokemonName = "charizard",
                        imageUrl = "",
                        trigger = "Level 36",
                        evolvesTo = emptyList()
                    )
                )
            )
        )
    )
)

@Preview(showBackground = true, name = "Detail - Fire type")
@Composable
private fun PokemonDetailContentFirePreview() {
    PokedexTheme {
        PokemonDetailContent(
            pokemon = previewPokemon,
            evolution = previewEvolution,
            onNavigateBack = {},
            onPokemonClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Water type")
@Composable
private fun PokemonDetailContentWaterPreview() {
    PokedexTheme {
        PokemonDetailContent(
            pokemon = previewPokemon.copy(
                id = "9",
                name = "blastoise",
                types = persistentListOf("water")
            ),
            evolution = previewEvolution,
            onNavigateBack = {},
            onPokemonClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Grass type")
@Composable
private fun PokemonDetailContentGrassPreview() {
    PokedexTheme {
        PokemonDetailContent(
            pokemon = previewPokemon.copy(
                id = "3",
                name = "venusaur",
                types = persistentListOf("grass", "poison")
            ),
            evolution = previewEvolution,
            onNavigateBack = {},
            onPokemonClick = {}
        )
    }
}
