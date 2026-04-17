package com.diegocunha.pokedex.feature.pokemon.domain.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PokemonEntry(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val types: ImmutableList<String> = persistentListOf()
)
