package com.diegocunha.pokedex.datasource.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegocunha.pokedex.datasource.db.entity.PokemonDetailEntity

@Dao
interface PokemonDetailDao {
    @Query("SELECT * FROM pokemon_detail WHERE id = :id")
    suspend fun getById(id: String): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PokemonDetailEntity)
}
