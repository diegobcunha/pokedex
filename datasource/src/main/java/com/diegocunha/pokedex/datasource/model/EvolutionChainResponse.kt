package com.diegocunha.pokedex.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvolutionChainResponse(
    val id: Int,
    val chain: ChainLink
)

@Serializable
data class ChainLink(
    val species: NamedResource,
    @SerialName("evolves_to") val evolvesTo: List<ChainLink>
)

@Serializable
data class NamedResource(
    val name: String,
    val url: String
)
