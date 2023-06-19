package com.br.diegocunha.pokedex.datasource.core

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SlotType(
    val type: Type
) : Parcelable

@Parcelize
data class Type(
    val name: PokemonType,
    val url: String
) : Parcelable

enum class PokemonType {
    @SerializedName("normal")
    NORMAL,

    @SerializedName("fighting")
    FIGHTING,

    @SerializedName("flying")
    FLYING,

    @SerializedName("poison")
    POISON,

    @SerializedName("ground")
    GROUND,

    @SerializedName("rock")
    ROCK,

    @SerializedName("bug")
    BUG,

    @SerializedName("ghost")
    GHOST,

    @SerializedName("steel")
    STEEL,

    @SerializedName("fire")
    FIRE,

    @SerializedName("water")
    WATER,

    @SerializedName("grass")
    GRASS,

    @SerializedName("electric")
    ELECTRIC,

    @SerializedName("psychic")
    PSYCHIC,

    @SerializedName("ice")
    ICE,

    @SerializedName("dragon")
    DRAGON,

    @SerializedName("dark")
    DARK,

    @SerializedName("fairy")
    FAIRY,

    @SerializedName("unknown")
    UNKNOWN,

    @SerializedName("shadow")
    SHADOW
}

@Parcelize
data class Sprites(
    val back_default: String? = null,
    val back_shiny: String? = null,
    val front_default: String? = null,
    val front_shiny: String? = null
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

@Parcelize
data class MoveType(
    val move: Move
): Parcelable

@Parcelize
data class Move(
    val name: String
): Parcelable