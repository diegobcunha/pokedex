package com.diegocunha.pokedex.datasource.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `pokemon_evolution` (
                `pokemonId` TEXT NOT NULL,
                `chainId` INTEGER NOT NULL,
                `chainJson` TEXT NOT NULL,
                PRIMARY KEY(`pokemonId`)
            )
            """.trimIndent()
        )
    }
}
