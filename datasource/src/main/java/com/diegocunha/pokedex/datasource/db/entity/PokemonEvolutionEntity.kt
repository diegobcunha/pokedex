package com.diegocunha.pokedex.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_evolution")
data class PokemonEvolutionEntity(
    @PrimaryKey val pokemonId: String,
    val chainId: Int,
    val chainJson: String
)