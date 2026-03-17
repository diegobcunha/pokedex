package com.diegocunha.pokedex.feature.pokemon.presentation.common

import androidx.compose.ui.graphics.Color

enum class PokemonType(val color: Color) {
    FIRE(Color(0xFFE25822)),
    WATER(Color(0xFF2196F3)),
    GRASS(Color(0xFF4CAF50)),
    POISON(Color(0xFF9C27B0)),
    FLYING(Color(0xFF7986CB)),
    ELECTRIC(Color(0xFFFFC107)),
    PSYCHIC(Color(0xFFE91E63)),
    ROCK(Color(0xFF8D6E63)),
    GROUND(Color(0xFF795548)),
    BUG(Color(0xFF8BC34A)),
    GHOST(Color(0xFF4527A0)),
    DARK(Color(0xFF4E342E)),
    DRAGON(Color(0xFF3F51B5)),
    STEEL(Color(0xFF607D8B)),
    ICE(Color(0xFF80DEEA)),
    FIGHTING(Color(0xFFB71C1C)),
    NORMAL(Color(0xFF9E9E9E)),
    FAIRY(Color(0xFFF48FB1)),
    UNKNOWN(Color(0xFF9E9E9E));

    companion object {
        fun fromName(name: String): PokemonType =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: UNKNOWN
    }
}
