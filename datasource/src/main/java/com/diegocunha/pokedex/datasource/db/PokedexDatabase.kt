package com.diegocunha.pokedex.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diegocunha.pokedex.datasource.db.dao.PokemonDetailDao
import com.diegocunha.pokedex.datasource.db.dao.PokemonEvolutionDao
import com.diegocunha.pokedex.datasource.db.dao.PokemonListEntryDao
import com.diegocunha.pokedex.datasource.db.dao.RemoteKeyDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonDetailEntity
import com.diegocunha.pokedex.datasource.db.entity.PokemonEvolutionEntity
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
import com.diegocunha.pokedex.datasource.db.entity.RemoteKeyEntity

@Database(
    entities = [
        PokemonListEntryEntity::class,
        RemoteKeyEntity::class,
        PokemonDetailEntity::class,
        PokemonEvolutionEntity::class
    ],
    version = 2
)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonListEntryDao(): PokemonListEntryDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
    abstract fun pokemonEvolutionDao(): PokemonEvolutionDao
}
