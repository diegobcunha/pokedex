package com.diegocunha.pokedex.datasource.di

import com.diegocunha.pokedex.datasource.network.createOkHttpClient
import com.diegocunha.pokedex.datasource.network.createRetrofit
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val datasourceModule = module {
    single { createOkHttpClient(isDebug = getProperty("isDebug", true)) }
    single { Json { ignoreUnknownKeys = true } }
    single { createRetrofit(okHttpClient = get(), json = get()) }
}
