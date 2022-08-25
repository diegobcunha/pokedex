package com.br.diegocunha.pokedex.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.br.diegocunha.pokedex.ui.components.PokeAppBar
import com.br.diegocunha.pokedex.ui.components.PokeDexCard
import com.br.diegocunha.pokedex.ui.components.ProgressIndicator
import com.br.diegocunha.pokedex.ui.components.items
import org.koin.androidx.compose.getViewModel

private const val COLUMN_COUNT = 2
private val GRID_SPACING = 8.dp

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel = getViewModel<HomeViewModel>()
    val response = viewModel.pagingFlow.collectAsLazyPagingItems()
    var search by remember {
        mutableStateOf("")
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        PokeAppBar(
            onSearch = { search = it },
            content = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(COLUMN_COUNT),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(GRID_SPACING),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(response) {
                        it?.let { PokeDexCard(pokemon = it, onPokemonClick = {}) }
                    }

                    response.apply {
                        when (loadState.append) {
                            is LoadState.Loading -> {
                                item { ProgressIndicator() }
                            }
                            else -> Unit
                        }

                        when (loadState.refresh) {
                            is LoadState.Loading -> {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ProgressIndicator()
                                    }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        )
    }
}