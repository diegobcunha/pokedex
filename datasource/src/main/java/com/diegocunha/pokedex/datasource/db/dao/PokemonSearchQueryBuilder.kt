package com.diegocunha.pokedex.datasource.db.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

object PokemonSearchQueryBuilder {

    fun build(nameQuery: String, typeNames: List<String>): SupportSQLiteQuery {
        val sb = StringBuilder("SELECT * FROM pokemon_list_entry WHERE 1=1")
        val args = mutableListOf<Any>()

        if (nameQuery.isNotBlank()) {
            sb.append(" AND name LIKE ?")
            args.add("%$nameQuery%")
        }

        if (typeNames.isNotEmpty()) {
            sb.append(" AND (")
            typeNames.forEachIndexed { index, type ->
                if (index > 0) sb.append(" OR ")
                sb.append("types LIKE ?")
                args.add("%\"$type\"%")
            }
            sb.append(")")
        }

        sb.append(" ORDER BY CAST(id AS INTEGER)")
        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }
}
