package com.br.diegocunha.pokedex.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.br.diegocunha.pokedex.R
import com.br.diegocunha.pokedex.datasource.core.Move
import com.br.diegocunha.pokedex.datasource.core.Type
import com.br.diegocunha.pokedex.ui.components.AboutPanelIconInfo
import com.br.diegocunha.pokedex.ui.components.AboutPanelLayout
import com.br.diegocunha.pokedex.ui.components.AboutPanelListInfo
import com.br.diegocunha.pokedex.ui.components.TextBadge
import com.br.diegocunha.pokedex.ui.components.pokemonColor
import com.br.diegocunha.pokedex.ui.model.PokemonUI
import com.br.diegocunha.pokedex.ui.theme.colorGrey100
import com.br.diegocunha.pokedex.ui.theme.colorRedPokeDex
import java.util.Locale

@Composable
fun Details(pokemonUI: PokemonUI) {
    val typeColor = pokemonUI.types.firstOrNull()?.pokemonColor() ?: Color(0xFF58ABF6)

    ConstraintLayout(
        modifier = Modifier
            .background(typeColor)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {

        val (pokeBallBackground, pokemonImage, pokemonInfo, infoContent) = createRefs()
        Image(
            modifier = Modifier.constrainAs(pokeBallBackground) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            },
            painter = painterResource(id = R.drawable.pokeball),
            alpha = 0.1F,
            colorFilter = ColorFilter.tint(colorGrey100),
            contentDescription = null
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .constrainAs(pokemonInfo) {
                    top.linkTo(pokeBallBackground.bottom, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 4.dp)
                    start.linkTo(parent.start, margin = 4.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                })

        Image(
            modifier = Modifier
                .size(200.dp)
                .constrainAs(pokemonImage) {
                    top.linkTo(parent.top, margin = 24.dp)
                    start.linkTo(parent.start, 80.dp)
                    end.linkTo(parent.end, 80.dp)
                },
            painter = rememberAsyncImagePainter(model = pokemonUI.sprites.front_default),
            contentDescription = null
        )

        DetailContent(
            modifier = Modifier
                .constrainAs(infoContent) {
                    top.linkTo(pokemonImage.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            pokemonUI = pokemonUI
        )
    }
}

@Composable
private fun DetailContent(
    modifier: Modifier = Modifier, pokemonUI: PokemonUI
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TypesDetails(modifier = Modifier.fillMaxWidth(), typeList = pokemonUI.types)
        AboutSection(pokemonUI)
    }
}

@Composable
private fun ColumnScope.AboutSection(pokemonUI: PokemonUI) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "About",
        color = Color.Black,
        textAlign = TextAlign.Center
    )

    AboutPokemon(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        weightText = pokemonUI.weight.toString(),
        heightText = pokemonUI.height.toString(),
        moves = pokemonUI.moves
    )
}

@Composable
private fun TypesDetails(modifier: Modifier = Modifier, typeList: List<Type>) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            16.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        typeList.forEach { type -> PokemonTypeBadge(type) }
    }
}

@Composable
private fun PokemonTypeBadge(type: Type) {
    TextBadge(
        text = type.name.name.toLowerCase().capitalize(locale = Locale.ROOT),
        backgroundColor = type.pokemonColor()
    )
}

@Composable
private fun AboutPokemon(
    modifier: Modifier = Modifier,
    weightText: String,
    heightText: String,
    moves: List<Move>
) {
    AboutPanelLayout(modifier = modifier, startSlot = {
        AboutPanelIconInfo(
            iconLabelPainter = painterResource(id = R.drawable.pokeball_s),
            textLabel = weightText,
            informationTitle = "Weight",
            iconColor = colorRedPokeDex
        )
    }, middleSlot = {
        AboutPanelIconInfo(
            iconLabelPainter = painterResource(id = R.drawable.pokeball_s),
            textLabel = heightText,
            informationTitle = "Height",
            iconColor = colorRedPokeDex
        )
    }, endSlot = {
        AboutPanelListInfo(
            infoList = moves.map { it.name },
            informationTitle = "Moves",
        )
    })
}