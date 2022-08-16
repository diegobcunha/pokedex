package com.br.diegocunha.pokedex.coroutine

import com.br.diegocunha.pokedex.templates.ProductionDispatchersProvider
import org.koin.dsl.module

val coroutineModule = module {
    factory<DispatchersProvider> { ProductionDispatchersProvider }
}