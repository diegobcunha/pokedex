package com.diegocunha.pokedex.datasource.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegocunha.pokedex.datasource.db.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeyEntity>)

    @Query("SELECT * FROM remote_keys WHERE pokemonId = :id")
    suspend fun remoteKeyFor(id: String): RemoteKeyEntity?

    @Query("SELECT createdAt FROM remote_keys ORDER BY createdAt ASC LIMIT 1")
    suspend fun oldestCreatedAt(): Long?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
