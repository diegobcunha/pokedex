package com.br.diegocunha.pokedex.ui.model

import android.os.Parcelable
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import com.br.diegocunha.pokedex.datasource.api.model.Sprites
import com.br.diegocunha.pokedex.datasource.api.model.Stats
import com.br.diegocunha.pokedex.datasource.api.model.Type
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonUI(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<Stats>,
    val height: Int,
    val weight: Int,
    val types: List<Type>
) : Parcelable

fun SinglePokemonResult.toPokemonUI() = PokemonUI(
    id = id,
    name = name.replaceFirstChar { it.uppercaseChar() },
    sprites = sprites,
    height = height,
    weight = weight,
    stats = stats,
    types = types.map { it.type }
)