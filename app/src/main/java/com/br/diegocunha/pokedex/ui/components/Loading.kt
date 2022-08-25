package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.br.diegocunha.pokedex.ui.theme.colorRedPokeDex

@Composable
fun ProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        color = colorRedPokeDex
    )
}