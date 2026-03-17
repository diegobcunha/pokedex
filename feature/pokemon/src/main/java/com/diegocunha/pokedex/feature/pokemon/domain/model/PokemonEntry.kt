package com.diegocunha.pokedex.feature.pokemon.domain.model

data class PokemonEntry(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val types: List<String> = emptyList()
)
