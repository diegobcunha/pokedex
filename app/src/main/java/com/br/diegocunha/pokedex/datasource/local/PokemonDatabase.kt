package com.br.diegocunha.pokedex.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.br.diegocunha.pokedex.datasource.local.model.PokemonEntity
import com.br.diegocunha.pokedex.datasource.local.model.PokemonInfoEntity

@Database(
    version = 1,
    exportSchema = true,
    entities = [PokemonEntity::class, PokemonInfoEntity::class],
)
abstract class PokemonDatabase: RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonInfoDao(): PokemonInfoDao
}