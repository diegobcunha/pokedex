package com.br.diegocunha.pokedex.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.br.diegocunha.pokedex.R
import com.br.diegocunha.pokedex.datasource.core.Type
import com.br.diegocunha.pokedex.ui.components.DefaultDivider
import com.br.diegocunha.pokedex.ui.components.DefaultScaffoldTopBar
import com.br.diegocunha.pokedex.ui.components.DefaultTitle
import com.br.diegocunha.pokedex.ui.components.ErrorView
import com.br.diegocunha.pokedex.ui.components.GetCrossfade
import com.br.diegocunha.pokedex.ui.components.PokeBallLarge
import com.br.diegocunha.pokedex.ui.components.PokemonTypeLabelsDetail
import com.br.diegocunha.pokedex.ui.components.ProgressIndicator
import com.br.diegocunha.pokedex.ui.components.RowTitle
import com.br.diegocunha.pokedex.ui.components.TypeLabelMetrics
import com.br.diegocunha.pokedex.ui.components.getPokemonId
import com.br.diegocunha.pokedex.ui.components.pokemonColor
import com.br.diegocunha.pokedex.ui.navigation.PokemonParam
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalFoundationApi::class)
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
                LazyColumn {
                    stickyHeader {
                        PokemonDetailHeader(
                            imageUrl = it.sprites.front_default,
                            pokemonName = it.name,
                            pokemonColor = it.types.first().pokemonColor()
                        )
                    }

                    item {
                        PokemonIndex(it.id)
                    }

                    item {
                        PokemonType(types = it.types)
                    }
                }
            }
        )
    }
}

@Composable
private fun PokemonDetailHeader(
    imageUrl: String?,
    pokemonName: String,
    pokemonColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = pokemonColor
            ),
        contentAlignment = Alignment.Center
    ) {
        PokeBallLarge(
            tint = Color.White,
            opacity = 0.25f
        )
        Image(
            modifier = Modifier.size(200.dp),
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = "${pokemonName}_image"
        )
    }
}

@Composable
private fun PokemonIndex(index: Int) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        RowTitle(
            text = stringResource(id = R.string.pokemon_index),
        )

        Text(getPokemonId(index))
        Spacer(modifier = Modifier.height(8.dp))
        DefaultDivider()
    }
}

@Composable
private fun PokemonType(types: List<Type>?) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        RowTitle(
            text = stringResource(id = R.string.pokemon_type_title),
        )

        Row(modifier = Modifier.padding(top = 8.dp)) {
            PokemonTypeLabelsDetail(types, TypeLabelMetrics.MEDIUM)
        }

        Spacer(modifier = Modifier.height(8.dp))
        DefaultDivider()
    }
}