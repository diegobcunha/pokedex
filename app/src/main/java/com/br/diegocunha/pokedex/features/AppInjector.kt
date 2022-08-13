package com.br.diegocunha.pokedex.features

import android.app.Application
import com.br.diegocunha.pokedex.datasource.dataSourceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

object AppInjector {

    fun inject(application: Application) {
        application.startInjection(
            dataSourceModule
        )
    }
}

private fun Application.startInjection(vararg module: Module) {
    startKoin {
        androidLogger(Level.ERROR)
        androidContext(this@startInjection)
        modules(listOf(*module))
    }
}