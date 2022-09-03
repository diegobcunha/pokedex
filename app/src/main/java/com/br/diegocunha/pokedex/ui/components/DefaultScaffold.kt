package com.br.diegocunha.pokedex.ui.components

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable

@Composable
fun DefaultScaffoldTopBar(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    title: @Composable () -> Unit,
    body: @Composable () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            DefaultTopAppBar(
                navigationIcon = {
                    BackNavigationIcon()
                },
                title = {
                    title()
                }
            )
        }
    ) { body() }
}

