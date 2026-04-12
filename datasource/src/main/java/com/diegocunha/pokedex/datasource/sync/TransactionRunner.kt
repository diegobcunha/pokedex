package com.diegocunha.pokedex.datasource.sync

fun interface TransactionRunner {
    suspend operator fun invoke(block: suspend () -> Unit)
}
