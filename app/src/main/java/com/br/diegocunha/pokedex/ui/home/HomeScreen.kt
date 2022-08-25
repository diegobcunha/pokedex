package com.br.diegocunha.pokedex.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.br.diegocunha.pokedex.ui.components.PokeAppBar
import com.br.diegocunha.pokedex.ui.components.PokeDexCard
import org.koin.androidx.compose.getViewModel

private const val COLUMN_COUNT = 2
private val GRID_SPACING = 8.dp

@Composable
@ExperimentalFoundationApi
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
                    columns = GridCells.Fixed(2)
                ) {
                    items(response) {
                        it?.let { PokeDexCard(pokemon = it, onPokemonClick = {}) }
                    }
                }
            }
        )
    }
}

@ExperimentalFoundationApi
private fun <T : Any> LazyGridScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyGridItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
    }
}