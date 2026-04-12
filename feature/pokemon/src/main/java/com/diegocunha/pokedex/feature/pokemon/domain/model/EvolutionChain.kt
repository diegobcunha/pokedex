package com.diegocunha.pokedex.feature.pokemon.domain.model

data class EvolutionChain(val base: EvolutionNode)

data class EvolutionNode(
    val pokemonId: String,
    val pokemonName: String,
    val imageUrl: String,
    val trigger: String?,
    val evolvesTo: List<EvolutionNode>
)