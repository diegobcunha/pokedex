package com.diegocunha.pokedex.feature.pokemon.domain.model

import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType

data class SearchFilter(
    val query: String = "",
    val selectedTypes: Set<PokemonType> = emptySet()
) {
    val isActive: Boolean get() = query.isNotBlank() || selectedTypes.isNotEmpty()
}
