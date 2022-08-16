package com.br.diegocunha.pokedex.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.br.diegocunha.pokedex.ui.components.PokeAppBar
import com.br.diegocunha.pokedex.ui.components.Pokemon
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
        val listState = rememberLazyGridState()
        PokeAppBar(
            onSearch = { search = it },
            content = {
                LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Fixed(COLUMN_COUNT),
                    horizontalArrangement = Arrangement.spacedBy(
                        GRID_SPACING,
                        Alignment.CenterHorizontally
                    ),
                    contentPadding = PaddingValues(
                        start = GRID_SPACING,
                        end = GRID_SPACING,
                        bottom = 4.dp
                    ), content = {
                        items(response.itemCount) { index ->
                            val pokemon = response.peek(index) ?: return@items
                            Pokemon(pokemonUI = pokemon)
                        }
                    }
                )
            }
        )
    }

}