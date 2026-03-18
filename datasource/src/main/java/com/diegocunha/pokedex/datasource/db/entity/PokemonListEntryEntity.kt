package com.diegocunha.pokedex.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_list_entry")
data class PokemonListEntryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    val types: String, // JSON List<String>
)
