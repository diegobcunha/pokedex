package com.br.diegocunha.pokedex.ui.components

import androidx.compose.material.Badge
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.br.diegocunha.pokedex.ui.theme.Typography
import com.br.diegocunha.pokedex.ui.theme.colorGrey100

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

@Composable
fun TextBadge(
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    backgroundColor: Color = colorGrey100,
    text: String
) {
    Badge(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        Text(text = text, style = MaterialTheme.typography.body2, color = textColor)
    }
}