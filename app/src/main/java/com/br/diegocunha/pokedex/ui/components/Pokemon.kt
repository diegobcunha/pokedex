package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.br.diegocunha.pokedex.datasource.api.model.PokemonType
import com.br.diegocunha.pokedex.datasource.api.model.Sprites
import com.br.diegocunha.pokedex.datasource.api.model.Type
import com.br.diegocunha.pokedex.ui.model.PokemonUI
import com.br.diegocunha.pokedex.ui.navigation.PokemonParam
import com.br.diegocunha.pokedex.ui.theme.colorWhite100
import com.br.diegocunha.pokedex.ui.theme.fontFamily

@Composable
fun PokeDexCard(pokemon: PokemonUI, onPokemonClick: (PokemonParam) -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onPokemonClick(pokemon.toParams()) },
        backgroundColor = pokemon.types.first().pokemonColor(),
        shape = RoundedCornerShape(16.dp)
    ) {
        PokeDexCardContent(pokemon = pokemon)
    }
}

@Composable
private fun PokeDexCardContent(pokemon: PokemonUI) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 8.dp, start = 12.dp)
        ) {
            PokemonName(text = pokemon.name)
            PokemonTypeLabels(pokemon.types.map { it.name.name }, TypeLabelMetrics.SMALL)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 12.dp)
        ) {
            PokemonId(pokemon.id.toString())
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 10.dp)
                .offset(x = 5.dp, y = 10.dp)
                .size(96.dp)
        ) {
            PokeBallSmall(
                Color.White,
                0.25f
            )
        }

        pokemon.sprites.front_default?.let { image ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp, end = 8.dp)
                    .size(72.dp)
            ) {
                Image(
                    modifier = Modifier.size(72.dp),
                    painter = rememberAsyncImagePainter(model = image),
                    contentDescription = "${pokemon.name}_image"
                )
            }
        }
    }

}

@Composable
private fun PokemonName(text: String?) {
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = text ?: "",
        style = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colorWhite100
        )
    )
}

@Composable
private fun PokemonId(text: String?) {
    Text(
        modifier = Modifier.alpha(0.1f),
        text = text ?: "",
        style = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    )
}

fun Type.pokemonColor() = when (this.name) {
    PokemonType.GRASS,
    PokemonType.BUG -> Color(0xFF2CDAB1)
    PokemonType.FIRE -> Color(0xFFF7786B)
    PokemonType.WATER,
    PokemonType.ICE -> Color(0xFF58ABF6)
    PokemonType.PSYCHIC,
    PokemonType.DRAGON,
    PokemonType.ELECTRIC -> Color(0xFFFFCE4B)
    PokemonType.POISON,
    PokemonType.GHOST -> Color(0xFF9F5BBA)
    PokemonType.DARK -> Color(0xFF303943)
    PokemonType.FIGHTING,
    PokemonType.GROUND -> Color(0xFFCA8179)
    else -> Color(0xFF58ABF6)
}

@Preview
@Composable
private fun PokeDexCardPreview() {
    val pokemon = PokemonUI(
        1,
        "bulbasaur",
        Sprites(front_default = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/1.png"),
        stats = emptyList(),
        height = 10,
        weight = 10,
        types = listOf(Type(name = PokemonType.GRASS, ""))
    )

    PokeDexCard(pokemon = pokemon, onPokemonClick = {})
}

private fun PokemonUI.toParams() = PokemonParam(
    id = id,
    name = name
)