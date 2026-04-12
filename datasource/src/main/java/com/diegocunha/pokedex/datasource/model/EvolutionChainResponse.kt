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
    @SerialName("evolution_details") val evolutionDetails: List<EvolutionDetail> = emptyList(),
    @SerialName("evolves_to") val evolvesTo: List<ChainLink>
)

@Serializable
data class EvolutionDetail(
    val trigger: NamedResource,
    @SerialName("min_level") val minLevel: Int? = null,
    val item: NamedResource? = null,
    @SerialName("held_item") val heldItem: NamedResource? = null,
    @SerialName("min_happiness") val minHappiness: Int? = null,
    @SerialName("min_beauty") val minBeauty: Int? = null,
    @SerialName("min_affection") val minAffection: Int? = null,
    @SerialName("time_of_day") val timeOfDay: String = "",
    @SerialName("known_move") val knownMove: NamedResource? = null,
    @SerialName("needs_overworld_rain") val needsOverworldRain: Boolean = false,
    @SerialName("turn_upside_down") val turnUpsideDown: Boolean = false
)

@Serializable
data class NamedResource(
    val name: String,
    val url: String
)
