package com.diegocunha.pokedex.feature.pokemon.domain.model

import kotlinx.collections.immutable.ImmutableList


data class Pokemon(
    val id: String,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: ImmutableList<String>,
    val stats: ImmutableList<PokemonStat>,
    val imageUrl: String?,
    val abilities: ImmutableList<String>
)
