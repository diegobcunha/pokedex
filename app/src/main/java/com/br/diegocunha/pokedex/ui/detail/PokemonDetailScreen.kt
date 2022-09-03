package com.br.diegocunha.pokedex.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.br.diegocunha.pokedex.ui.components.DefaultScaffoldTopBar
import com.br.diegocunha.pokedex.ui.components.ErrorView
import com.br.diegocunha.pokedex.ui.components.GetCrossfade
import com.br.diegocunha.pokedex.ui.components.ProgressIndicator
import com.br.diegocunha.pokedex.ui.components.pokemonColor
import com.br.diegocunha.pokedex.ui.navigation.PokemonParam
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonDetailScreen(navController: NavController, params: PokemonParam) {
    val viewModel = getViewModel<PokemonDetailViewModel>() { parametersOf(params.id) }
    val viewModelState by viewModel.stateFlow.collectAsState()

    DefaultScaffoldTopBar(
        title = {
            Text(params.name)
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
                LazyColumn {
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    it.types
                                        .first()
                                        .pokemonColor()
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(200.dp),
                                painter = rememberAsyncImagePainter(model = it.sprites.front_default),
                                contentDescription = "${it.name}_image"
                            )
                        }
                    }
                }
            }
        )
    }


}