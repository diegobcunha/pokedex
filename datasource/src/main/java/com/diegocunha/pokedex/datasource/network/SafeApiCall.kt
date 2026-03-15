package com.diegocunha.pokedex.datasource.network

import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.core.toResource
import kotlinx.coroutines.withContext

suspend fun <T> safeApiCall(
    dispatcher: DispatchersProvider,
    call: suspend () -> T
): Resource<T> = withContext(dispatcher.io()) {
    runCatching { call() }.toResource()
}
