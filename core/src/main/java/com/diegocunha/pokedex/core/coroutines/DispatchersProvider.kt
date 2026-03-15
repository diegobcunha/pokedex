package com.diegocunha.pokedex.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProvider {

    fun io(): CoroutineDispatcher

    fun main(): CoroutineDispatcher
}