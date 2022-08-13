package com.br.diegocunha.pokedex.datasource.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PokemonResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Pokemon>
) : Parcelable

@Parcelize
data class Pokemon(
    val name: String,
    val url: String
) : Parcelable

@Parcelize
data class SinglePokemonResult(
    val sprites: Sprites,
    val stats: List<Stats>,
    val height: Int,
    val weight: Int
) : Parcelable

@Parcelize
data class Sprites(
    val back_default: String,
    val back_shiny: String,
    val front_default: String,
    val front_shiny: String
) : Parcelable

@Parcelize
data class Stats(
    val base_stat: Int,
    val effort: Int,
    val stat: Stat
) : Parcelable

@Parcelize
data class Stat(
    val name: String,
    val url: String
) : Parcelable