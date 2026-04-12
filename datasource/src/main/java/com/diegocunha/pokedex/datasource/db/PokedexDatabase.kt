package com.diegocunha.pokedex.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diegocunha.pokedex.datasource.db.dao.PokemonDetailDao
import com.diegocunha.pokedex.datasource.db.dao.PokemonEvolutionDao
import com.diegocunha.pokedex.datasource.db.dao.PokemonListEntryDao
import com.diegocunha.pokedex.datasource.db.dao.SyncStateDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonDetailEntity
import com.diegocunha.pokedex.datasource.db.entity.PokemonEvolutionEntity
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.db.entity.SyncStateEntity

@Database(
    entities = [
        PokemonListEntryEntity::class,
        PokemonDetailEntity::class,
        PokemonEvolutionEntity::class,
        SyncStateEntity::class
    ],
    version = 3
)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonListEntryDao(): PokemonListEntryDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
    abstract fun pokemonEvolutionDao(): PokemonEvolutionDao
    abstract fun syncStateDao(): SyncStateDao
}
