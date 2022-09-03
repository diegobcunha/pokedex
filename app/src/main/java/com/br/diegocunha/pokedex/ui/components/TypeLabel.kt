package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.diegocunha.pokedex.datasource.api.model.Type
import com.br.diegocunha.pokedex.ui.theme.colorWhite100
import com.br.diegocunha.pokedex.ui.theme.fontFamily

data class TypeLabelMetrics(
    val cornerRadius: Dp,
    val fontSize: TextUnit,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
    val elementSpacing: Dp
) {
    companion object {
        val SMALL = TypeLabelMetrics(24.dp, 9.sp, 3.dp, 8.dp, 8.dp)
        val MEDIUM = TypeLabelMetrics(24.dp, 12.sp, 4.dp, 12.dp, 8.dp)
    }
}

@Composable
fun PokemonTypeLabels(types: List<String>?, metrics: TypeLabelMetrics) {
    types?.forEach {
        Surface(
            color = Color(0x38FFFFFF),
            shape = RoundedCornerShape(metrics.cornerRadius)
        ) {
            PokemonTypeLabel(it, metrics)
        }
        Spacer(modifier = Modifier.size(metrics.elementSpacing))
    }
}

@Composable
fun PokemonTypeLabelsDetail(types: List<Type>?, metrics: TypeLabelMetrics) {
    types?.forEach {
        Surface(
            color = it.pokemonColor(),
            shape = RoundedCornerShape(metrics.cornerRadius)
        ) {
            PokemonTypeLabel(it.name.name, metrics)
        }
        Spacer(modifier = Modifier.size(metrics.elementSpacing))
    }
}

@Composable
fun PokemonTypeLabel(text: String, metrics: TypeLabelMetrics) {
    Text(
        modifier = Modifier.padding(
            horizontal = metrics.horizontalPadding,
            vertical = metrics.verticalPadding
        ),
        text = text,
        style = TextStyle(
            fontFamily = fontFamily,
            fontSize = metrics.fontSize,
            color = colorWhite100
        )
    )
}