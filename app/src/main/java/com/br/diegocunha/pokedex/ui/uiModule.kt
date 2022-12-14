package com.br.diegocunha.pokedex.ui

import com.br.diegocunha.pokedex.ui.detail.PokemonDetailViewModel
import com.br.diegocunha.pokedex.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { (pokemonId: Int) -> PokemonDetailViewModel(get(), get(), pokemonId) }
}