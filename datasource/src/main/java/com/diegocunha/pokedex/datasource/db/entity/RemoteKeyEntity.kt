package com.diegocunha.pokedex.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val pokemonId: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val createdAt: Long = System.currentTimeMillis()
)
