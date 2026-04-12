package com.diegocunha.pokedex.feature.pokemon.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.diegocunha.pokedex.coreui.theme.spacing
import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType

private val SELECTABLE_TYPES = PokemonType.entries.filter { it != PokemonType.UNKNOWN }

@Composable
fun TypeFilterRow(
    selectedTypes: Set<PokemonType>,
    onTypeToggle: (PokemonType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        items(SELECTABLE_TYPES, key = { it.name }) { type ->
            val selected = type in selectedTypes
            FilterChip(
                selected = selected,
                onClick = { onTypeToggle(type) },
                label = {
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = type.color,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}
