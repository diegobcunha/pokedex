package com.br.diegocunha.pokedex.datasource

import com.br.diegocunha.pokedex.BuildConfig
import com.br.diegocunha.pokedex.datasource.api.CallAdapterFactory
import com.br.diegocunha.pokedex.datasource.api.PokeDexAPI
import com.br.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.br.diegocunha.pokedex.datasource.repository.PokemonRepositoryImpl
import com.br.diegocunha.pokedex.datasource.source.PokemonSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = "https://pokeapi.co/api/v2/"

val dataSourceModule = module {

    single { GsonBuilder().create() }

    factory {
        okHttp3()
    }

    single { retrofit(get(), get()).create(PokeDexAPI::class.java) }

    factory<PokemonRepository> { PokemonRepositoryImpl(get(), get()) }

    factory { PokemonSource(get()) }
}

private fun Scope.okHttp3(): OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.addInterceptor { chain ->
        val url = chain
            .request()
            .url
            .newBuilder()
            .build()
        chain.proceed(chain.request().newBuilder().url(url).build())
    }
    val level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }

    val logger = HttpLoggingInterceptor()
    logger.level = level

    builder.interceptors().add(logger)
    return builder.build()
}

private fun Scope.retrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CallAdapterFactory())
        .build()
}