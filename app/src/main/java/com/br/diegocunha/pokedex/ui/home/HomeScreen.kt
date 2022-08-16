package com.br.diegocunha.pokedex.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.br.diegocunha.pokedex.ui.components.PokeAppBar
import org.koin.androidx.compose.getViewModel

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
                LazyColumn {
                    items(response) {
                        Text(it?.name ?: "Pokemon")
                    }
                }
            }
        )
    }

}