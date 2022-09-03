package com.br.diegocunha.pokedex.datasource.api.model

import android.os.Parcelable
import com.br.diegocunha.pokedex.datasource.core.SlotType
import com.br.diegocunha.pokedex.datasource.core.Sprites
import com.br.diegocunha.pokedex.datasource.core.Stats
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonResponse(
    val count: Int,
    val next: String,
    val previous: String?,
    val results: List<PokemonResult>
) : Parcelable

fun PokemonResponse.nextPage() =
    next.substringAfter(delimiter = "offset=").substringBefore(delimiter = "&").toInt()

fun PokemonResponse.prevPage() = if (!previous.isNullOrEmpty()) {
    previous.substringAfter(delimiter = "offset=").substringBefore(delimiter = "&").toInt()
} else {
    null
}

@Parcelize
data class PokemonResult(
    val name: String,
    val url: String
) : Parcelable

@Parcelize
data class SinglePokemonResult(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<Stats>,
    val height: Int,
    val weight: Int,
    val types: List<SlotType>
) : Parcelable
