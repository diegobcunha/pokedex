package com.br.diegocunha.pokedex

import android.app.Application
import com.br.diegocunha.pokedex.features.AppInjector

class PokeDexApplication: Application()  {

    override fun onCreate() {
        super.onCreate()
        AppInjector.inject(this)
    }

}