package com.diegocunha.pokedex.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonSpeciesResponse(
    val id: Int,
    @SerialName("evolution_chain") val evolutionChain: EvolutionChainRef
)

@Serializable
data class EvolutionChainRef(val url: String)