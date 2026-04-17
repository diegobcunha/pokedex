package com.diegocunha.pokedex.feature.pokemon.domain.model

import com.diegocunha.pokedex.feature.pokemon.presentation.common.PokemonType
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class SearchFilter(
    val query: String = "",
    val selectedTypes: ImmutableSet<PokemonType> = persistentSetOf()
) {
    val isActive: Boolean get() = query.isNotBlank() || selectedTypes.isNotEmpty()
}
