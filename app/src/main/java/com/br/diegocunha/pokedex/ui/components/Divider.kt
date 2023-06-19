package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DefaultDivider(color: Color = Color.LightGray) {
    Divider(color = color)
}

@Composable
fun DividerVertical(modifier: Modifier = Modifier, color: Color = Color.LightGray) {
    Divider(
        modifier = modifier
            .fillMaxHeight()
            .width(1.dp),
        color = color
    )
}