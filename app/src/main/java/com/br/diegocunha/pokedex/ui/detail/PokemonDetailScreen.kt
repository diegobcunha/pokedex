package com.br.diegocunha.pokedex.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.br.diegocunha.pokedex.ui.components.DefaultScaffoldTopBar
import com.br.diegocunha.pokedex.ui.components.DefaultTitle
import com.br.diegocunha.pokedex.ui.components.ErrorView
import com.br.diegocunha.pokedex.ui.components.GetCrossfade
import com.br.diegocunha.pokedex.ui.components.ProgressIndicator
import com.br.diegocunha.pokedex.ui.navigation.PokemonParam
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PokemonDetailScreen(params: PokemonParam) {
    val viewModel = koinViewModel<PokemonDetailViewModel>() { parametersOf(params.id) }
    val viewModelState by viewModel.stateFlow.collectAsState()

    DefaultScaffoldTopBar(
        title = {
            DefaultTitle(params.name)
        },
    ) {
        GetCrossfade(
            state = viewModelState,
            initial = {
                ProgressIndicator()
            },
            failure = {
                ErrorView() {
                    viewModel.retry()
                }
            },
            success = {
                Details(it)
            }
        )
    }
}