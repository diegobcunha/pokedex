package com.diegocunha.pokedex.datasource.sync

sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Success : SyncState()
    data class Error(val exception: Throwable) : SyncState()
}
