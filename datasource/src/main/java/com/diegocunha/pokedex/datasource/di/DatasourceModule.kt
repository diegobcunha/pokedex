package com.diegocunha.pokedex.datasource.di

import com.diegocunha.pokedex.datasource.BuildConfig
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.datasource.network.createOkHttpClient
import com.diegocunha.pokedex.datasource.network.createRetrofit
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.diegocunha.pokedex.datasource.repository.PokemonRepositoryImpl
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import retrofit2.Retrofit

val datasourceModule = module {
    single { createOkHttpClient(isDebug = BuildConfig.DEBUG) }
    single { Json { ignoreUnknownKeys = true } }
    single { createRetrofit(okHttpClient = get(), json = get()) }
    single { get<Retrofit>().create(PokemonApiService::class.java) }
    single<PokemonRepository> { PokemonRepositoryImpl(get(), get()) }
}
