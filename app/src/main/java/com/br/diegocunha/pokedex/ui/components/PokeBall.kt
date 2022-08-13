package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import com.br.diegocunha.pokedex.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun PokeBallLarge(tint: Color, opacity: Float = 1f) {
    PokeBall(tint, opacity, R.drawable.pokeball)
}

@Composable
fun PokeBallSmall(tint: Color, opacity: Float = 1f) {
    PokeBall(tint, opacity, R.drawable.pokeball_s)
}

@Composable
private fun PokeBall(tint: Color, opacity: Float, imageResId: Int) {
    LoadImage(imageResId, tint, opacity)
}

@Preview
@Composable
fun PreviewPokeBall() {
    Box(
        Modifier.size(100.dp)
    ) {
        PokeBall(tint = Color.Black, opacity = 1f, imageResId = R.drawable.pokeball)
    }
}