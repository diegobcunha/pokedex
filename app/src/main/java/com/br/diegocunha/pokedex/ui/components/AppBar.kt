package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun PokeAppBar(
    onSearch: (String) -> Unit
) {
    AppBar(background = { PokeBallBackground() }) {

        Column(
            Modifier.padding(
                top = 32.dp,
                start = 32.dp,
                end = 32.dp,
                bottom = 16.dp
            )
        ) {
            SearchBar(
                hint = "Search your Pokemon...",
                onSearch = onSearch
            )
        }

    }
}

@Composable
private fun AppBar(
    background: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(
            bottomStart = 32.dp,
            bottomEnd = 32.dp
        )
    ) {
        Box {
            background()

            Box(contentAlignment = Alignment.TopCenter) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun PokeAppBarPreview() {
    PokeAppBar(onSearch = {})
}