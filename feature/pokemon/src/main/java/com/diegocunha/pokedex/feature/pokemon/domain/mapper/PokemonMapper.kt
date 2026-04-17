package com.diegocunha.pokedex.feature.pokemon.domain.mapper

import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.feature.pokemon.domain.model.Pokemon
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonStat
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun PokemonEntryResponse.toDomain(): PokemonEntry = PokemonEntry(
    id = url.trimEnd('/').substringAfterLast('/'),
    name = name
)

fun PokemonEntryResponse.toDomain(detail: PokemonResponse): PokemonEntry = PokemonEntry(
    id = url.trimEnd('/').substringAfterLast('/'),
    name = name,
    imageUrl = detail.sprites.frontDefault,
    types = detail.types.sortedBy { it.slot }.map { it.type.name }.toImmutableList()
)

fun PokemonListEntryEntity.toDomain(): PokemonEntry = PokemonEntry(
    id = id,
    name = name,
    imageUrl = imageUrl,
    types = json.decodeFromString(ListSerializer(String.serializer()), types).toImmutableList()
)

fun PokemonResponse.toDomain(): Pokemon = Pokemon(
    id = id.toString(),
    name = name,
    height = height,
    weight = weight,
    types = types.map { it.type.name }.toImmutableList(),
    stats = stats.map { PokemonStat(name = it.stat.name, value = it.baseStat) }.toImmutableList(),
    imageUrl = sprites.frontDefault,
    abilities = abilities.map { it.ability.name }.toImmutableList()
)
