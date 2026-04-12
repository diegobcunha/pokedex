package com.diegocunha.pokedex.feature.pokemon.presentation.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.diegocunha.pokedex.coreui.theme.spacing
import com.diegocunha.pokedex.feature.pokemon.domain.model.EvolutionChain
import com.diegocunha.pokedex.feature.pokemon.domain.model.EvolutionNode

@Composable
fun EvolutionSection(
    evolution: EvolutionChain,
    onPokemonClick: (pokemonId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Evolution Chain",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
        EvolutionNodeTree(node = evolution.base, onPokemonClick = onPokemonClick)
        if (evolution.base.evolvesTo.isEmpty()) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
            Text(
                text = "This Pokémon does not evolve.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EvolutionNodeTree(
    node: EvolutionNode,
    onPokemonClick: (pokemonId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EvolutionNodeItem(node = node, onPokemonClick = onPokemonClick)

        if (node.evolvesTo.isNotEmpty()) {
            if (node.evolvesTo.size == 1) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                EvolutionArrow(label = node.evolvesTo.first().trigger)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                EvolutionNodeTree(
                    node = node.evolvesTo.first(),
                    onPokemonClick = onPokemonClick
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    node.evolvesTo.forEachIndexed { index, child ->
                        if (index > 0) Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                            EvolutionArrow(label = child.trigger)
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                            EvolutionNodeTree(
                                node = child,
                                onPokemonClick = onPokemonClick,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EvolutionNodeItem(
    node: EvolutionNode,
    onPokemonClick: (pokemonId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onPokemonClick(node.pokemonId) }
            .padding(MaterialTheme.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = node.imageUrl,
            contentDescription = node.pokemonName,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = node.pokemonName.replaceFirstChar(Char::uppercaseChar),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EvolutionArrow(
    label: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "▼",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}