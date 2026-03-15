package com.diegocunha.pokedex.feature.pokemon.domain.model

data class Pokemon(
    val id: String,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<String>,
    val stats: List<PokemonStat>,
    val imageUrl: String?,
    val abilities: List<String>
)
