package com.diegocunha.pokedex.feature.pokemon.domain.mapper

import com.diegocunha.pokedex.datasource.model.ChainLink
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.EvolutionDetail
import com.diegocunha.pokedex.feature.pokemon.domain.model.EvolutionChain
import com.diegocunha.pokedex.feature.pokemon.domain.model.EvolutionNode

private const val SPRITE_BASE_URL =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%s.png"

fun EvolutionChainResponse.toDomain(): EvolutionChain =
    EvolutionChain(base = chain.toNode(trigger = null))

private fun ChainLink.toNode(trigger: String?): EvolutionNode {
    val speciesId = species.url.trimEnd('/').substringAfterLast('/')
    return EvolutionNode(
        pokemonId = speciesId,
        pokemonName = species.name,
        imageUrl = SPRITE_BASE_URL.format(speciesId),
        trigger = trigger,
        evolvesTo = evolvesTo.map { child ->
            child.toNode(trigger = child.evolutionDetails.firstOrNull()?.toTriggerLabel())
        }
    )
}

private fun EvolutionDetail.toTriggerLabel(): String {
    minLevel?.let { return "Level $it" }
    item?.let { return "Use ${it.name.formatEvolutionName()}" }
    heldItem?.let { return "Hold ${it.name.formatEvolutionName()}" }
    minHappiness?.let { return "High Friendship" }
    minBeauty?.let { return "High Beauty" }
    minAffection?.let { return "High Affection" }
    if (timeOfDay.isNotEmpty()) {
        return when (timeOfDay) {
            "day" -> "Daytime"
            "night" -> "Nighttime"
            else -> timeOfDay.formatEvolutionName()
        }
    }
    knownMove?.let { return "Know ${it.name.formatEvolutionName()}" }
    if (needsOverworldRain) return "Rain"
    if (turnUpsideDown) return "Turn Upside Down"
    return trigger.name.formatEvolutionName()
}

private fun String.formatEvolutionName(): String =
    split('-').joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }