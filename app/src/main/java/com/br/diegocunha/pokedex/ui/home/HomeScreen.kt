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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.br.diegocunha.pokedex.ui.components.PokeAppBar
import com.br.diegocunha.pokedex.ui.components.PokeDexCard
import com.br.diegocunha.pokedex.ui.components.makeLoadingContent
import com.br.diegocunha.pokedex.ui.navigation.PokeScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(onPokemonSelected: (String) -> Unit) {
    val viewModel = koinViewModel<HomeViewModel>()
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

                    items(
                        count = response.itemCount,
                        key = response.itemKey { it.id },
                    ) {index ->
                        val item = response[index]
                        PokeDexCard(pokemon = item, onPokemonClick = {
                            val route = PokeScreen.PokemonDetail.navigate(it)
                            onPokemonSelected(route)
                        })

                        Spacer(Modifier.height(8.dp))
                    }

                    makeLoadingContent(response)
                }
            }
        )
    }
}