package com.br.diegocunha.pokedex.ui.detail

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.br.diegocunha.pokedex.ui.components.ErrorView
import com.br.diegocunha.pokedex.ui.components.GetCrossfade
import com.br.diegocunha.pokedex.ui.components.ProgressIndicator
import com.br.diegocunha.pokedex.ui.navigation.PokemonParam
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PokemonDetailScreen(navController: NavController, params: PokemonParam) {
    val viewModel = getViewModel<PokemonDetailViewModel>() { parametersOf(params.id) }
    val viewModelState by viewModel.stateFlow.collectAsState()

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
            LazyColumn {
                item { Text(it.name) }
            }
        }
    )
}