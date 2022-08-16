package com.br.diegocunha.pokedex.ui.components

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.br.diegocunha.pokedex.datasource.api.model.PokemonType
import com.br.diegocunha.pokedex.datasource.api.model.Type
import com.br.diegocunha.pokedex.ui.home.PokemonUI

@Composable
fun Pokemon(pokemonUI: PokemonUI) {
    Log.d("PokemonError", pokemonUI.name)
    Card(
        backgroundColor = pokemonUI.types.first().pokemonColor(),
        shape = RoundedCornerShape(32.dp)
    ) {
        Text(pokemonUI.name)
    }
}

private fun Type.pokemonColor() = when (this.name) {
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

/**
 *     <color name="poke_black">#303943</color>
<color name="poke_blue">#429BED</color>
<color name="poke_brown">#B1736C</color>
<color name="poke_light_blue">#58ABF6</color>
<color name="poke_light_brown">#CA8179</color>
<color name="poke_light_purple">#9F5BBA</color>
<color name="poke_light_red">#F7786B</color>
<color name="poke_light_teal">#2CDAB1</color>
<color name="poke_light_yellow">#FFCE4B</color>
<color name="poke_purple">#7C538C</color>
 */