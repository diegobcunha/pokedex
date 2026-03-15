package com.diegocunha.pokedex.feature.pokemon.domain.mapper

import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.feature.pokemon.domain.model.Pokemon
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonStat

fun PokemonEntryResponse.toDomain(): PokemonEntry = PokemonEntry(
    id = url.trimEnd('/').substringAfterLast('/'),
    name = name
)

fun PokemonResponse.toDomain(): Pokemon = Pokemon(
    id = id.toString(),
    name = name,
    height = height,
    weight = weight,
    types = types.map { it.type.name },
    stats = stats.map { PokemonStat(name = it.stat.name, value = it.baseStat) },
    imageUrl = sprites.frontDefault,
    abilities = abilities.map { it.ability.name }
)
