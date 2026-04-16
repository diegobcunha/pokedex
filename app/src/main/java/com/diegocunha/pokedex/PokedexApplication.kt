package com.diegocunha.pokedex

import android.app.Application
import com.diegocunha.pokedex.core.di.coreModule
import com.diegocunha.pokedex.coreui.di.coreUiModule
import com.diegocunha.pokedex.datasource.di.datasourceModule
import com.diegocunha.pokedex.feature.pokemon.di.pokemonModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PokedexApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@PokedexApplication)
            modules(
                coreModule,
                coreUiModule,
                datasourceModule,
                pokemonModule
            )
        }
    }
}
