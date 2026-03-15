package com.diegocunha.pokedex.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonEntryResponse>
)

@Serializable
data class PokemonEntryResponse(
    val name: String,
    val url: String
)
