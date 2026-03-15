package com.diegocunha.pokedex.feature.pokemon.di

import com.diegocunha.pokedex.feature.pokemon.presentation.list.PokemonListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pokemonModule = module {
    viewModel { PokemonListViewModel(get()) }
}
