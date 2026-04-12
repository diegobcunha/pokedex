package com.diegocunha.pokedex.datasource.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity

@Dao
interface PokemonListEntryDao {
    @Query("SELECT * FROM pokemon_list_entry ORDER BY CAST(id AS INTEGER)")
    fun pagingSource(): PagingSource<Int, PokemonListEntryEntity>

    @RawQuery(observedEntities = [PokemonListEntryEntity::class])
    fun pagingSourceFiltered(query: SupportSQLiteQuery): PagingSource<Int, PokemonListEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<PokemonListEntryEntity>)

    @Query("DELETE FROM pokemon_list_entry")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM pokemon_list_entry")
    suspend fun count(): Int

    @Query("DELETE FROM pokemon_list_entry WHERE CAST(id AS INTEGER) > :offset")
    suspend fun deleteEntriesAfterOffset(offset: Int)
}
