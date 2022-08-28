package com.br.diegocunha.pokedex.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.br.diegocunha.pokedex.ui.components.PokeAppBar
import com.br.diegocunha.pokedex.ui.components.PokeDexCard
import com.br.diegocunha.pokedex.ui.components.makeLoadingContent
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel = getViewModel<HomeViewModel>()
    val response = viewModel.pagingFlow.collectAsLazyPagingItems()

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        PokeAppBar(
            onSearch = {
                viewModel.updateCurrentSearch(it)
                response.retry()
            },
            content = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(response) {
                        it?.let { PokeDexCard(pokemon = it, onPokemonClick = {}) }
                        Spacer(Modifier.height(8.dp))
                    }

                    makeLoadingContent(response)
                }
            }
        )
    }
}