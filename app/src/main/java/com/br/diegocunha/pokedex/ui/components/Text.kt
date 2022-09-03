package com.br.diegocunha.pokedex.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.br.diegocunha.pokedex.ui.theme.Typography

@Composable
fun DefaultTitle(text: String) {
    Text(text, fontWeight = FontWeight.Bold)
}
@Composable
fun RowTitle(text: String) {
    Text(
        text = text,
        style = Typography.h5,
        fontWeight = FontWeight.Bold
    )
}