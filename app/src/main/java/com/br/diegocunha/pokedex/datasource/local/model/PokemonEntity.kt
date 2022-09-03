package com.br.diegocunha.pokedex.datasource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.br.diegocunha.pokedex.datasource.core.SlotType
import com.br.diegocunha.pokedex.datasource.core.Sprites
import com.br.diegocunha.pokedex.datasource.core.Stats

@Entity
data class PokemonEntity(
    var page: Int = 0,
    @PrimaryKey val name: String,
    val url: String
)

@Entity
data class PokemonInfoEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<Stats>,
    val height: Int,
    val weight: Int,
    val types: List<SlotType>
)