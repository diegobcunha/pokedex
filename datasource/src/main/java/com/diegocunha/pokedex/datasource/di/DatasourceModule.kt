package com.diegocunha.pokedex.datasource.di

import androidx.room.Room
import androidx.room.withTransaction
import com.diegocunha.pokedex.datasource.BuildConfig
import com.diegocunha.pokedex.datasource.db.MIGRATION_1_2
import com.diegocunha.pokedex.datasource.db.MIGRATION_2_3
import com.diegocunha.pokedex.datasource.db.PokedexDatabase
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.datasource.network.createOkHttpClient
import com.diegocunha.pokedex.datasource.network.createRetrofit
import com.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.diegocunha.pokedex.datasource.repository.PokemonRepositoryImpl
import com.diegocunha.pokedex.datasource.sync.PokemonSyncManager
import com.diegocunha.pokedex.datasource.sync.TransactionRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val datasourceModule = module {
    single { createOkHttpClient(isDebug = BuildConfig.DEBUG) }
    single { Json { ignoreUnknownKeys = true } }
    single { createRetrofit(okHttpClient = get(), json = get()) }
    single { get<Retrofit>().create(PokemonApiService::class.java) }
    single {
        Room.databaseBuilder(androidContext(), PokedexDatabase::class.java, "pokedex.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }
    single { get<PokedexDatabase>().pokemonListEntryDao() }
    single { get<PokedexDatabase>().pokemonDetailDao() }
    single { get<PokedexDatabase>().pokemonEvolutionDao() }
    single { get<PokedexDatabase>().syncStateDao() }
    single<PokemonRepository> { PokemonRepositoryImpl(get(), get(), get(), get(), get()) }
    single(named("SyncScope")) { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single<TransactionRunner> { TransactionRunner { block -> get<PokedexDatabase>().withTransaction(block) } }
    single {
        PokemonSyncManager(
            apiService = get(),
            listEntryDao = get(),
            syncStateDao = get(),
            transactionRunner = get(),
            syncScope = get(named("SyncScope")),
            dispatchers = get()
        )
    }
}
