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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `remote_keys`")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `sync_state` (
                `id` INTEGER NOT NULL,
                `status` TEXT NOT NULL,
                `previousCount` INTEGER NOT NULL,
                `syncedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}
