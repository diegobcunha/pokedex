package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.br.diegocunha.pokedex.ui.theme.colorGrey100

@Composable
fun PokeBallBackground() {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxSize()
            .offset(x = 90.dp, y = (-70).dp)

    ) {
        Box(
            Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
        ) {
            PokeBallLarge(colorGrey100)
        }
    }
}