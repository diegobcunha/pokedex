package com.br.diegocunha.pokedex.templates

import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides the production dispatchers
 */
object ProductionDispatchersProvider :
    DispatchersProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
}