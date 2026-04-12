package com.diegocunha.pokedex.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val id: Int = 1,
    val status: String,
    val previousCount: Int,
    val syncedAt: Long = System.currentTimeMillis()
)
