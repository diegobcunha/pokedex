package com.diegocunha.pokedex.core.di

import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.core.coroutines.DispatchersProviderImpl
import org.koin.dsl.module

val coreModule = module {
    single<DispatchersProvider> { DispatchersProviderImpl }
}
