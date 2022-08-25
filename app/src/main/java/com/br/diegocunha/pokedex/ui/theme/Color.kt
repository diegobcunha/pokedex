package com.br.diegocunha.pokedex.ui.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val colorGrey100 = Color(0xFFF5F5F5)
val colorWhite100 = Color(0xFFFFFFFF)
val colorRedPokeDex = Color(0xFFC20029)

internal val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = colorRedPokeDex,
    onPrimary = Color.Black,
    secondary = Color.White,
    onSecondary = Color.Black,
    background = Color(0xFFEEEEEE),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFD00036),
    onError = Color.White
)
