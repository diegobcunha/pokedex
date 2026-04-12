package com.diegocunha.pokedex.datasource.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegocunha.pokedex.datasource.db.entity.PokemonEvolutionEntity

@Dao
interface PokemonEvolutionDao {

    @Query("SELECT * FROM pokemon_evolution WHERE pokemonId = :pokemonId")
    suspend fun getByPokemonId(pokemonId: String): PokemonEvolutionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PokemonEvolutionEntity)
}