package com.diegocunha.pokedex.datasource.di

import androidx.room.Room
import com.diegocunha.pokedex.datasource.BuildConfig
import com.diegocunha.pokedex.datasource.db.MIGRATION_1_2
import com.diegocunha.pokedex.datasource.db.PokedexDatabase
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.datasource.network.createOkHttpClient
import com.diegocunha.pokedex.datasource.network.createRetrofit
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.diegocunha.pokedex.datasource.repository.PokemonRepositoryImpl
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

val datasourceModule = module {
    single { createOkHttpClient(isDebug = BuildConfig.DEBUG) }
    single { Json { ignoreUnknownKeys = true } }
    single { createRetrofit(okHttpClient = get(), json = get()) }
    single { get<Retrofit>().create(PokemonApiService::class.java) }
    single {
        Room.databaseBuilder(androidContext(), PokedexDatabase::class.java, "pokedex.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    single { get<PokedexDatabase>().pokemonListEntryDao() }
    single { get<PokedexDatabase>().remoteKeyDao() }
    single { get<PokedexDatabase>().pokemonDetailDao() }
    single { get<PokedexDatabase>().pokemonEvolutionDao() }
    single<PokemonRepository> { PokemonRepositoryImpl(get(), get(), get(), get(), get()) }
}
