package com.diegocunha.pokedex.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: String,      // JSON List<String>
    val stats: String,      // JSON List<{name, baseStat}>
    val abilities: String,  // JSON List<String>
    val imageUrl: String?,
    val lastFetched: Long
)
