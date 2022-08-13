package com.br.diegocunha.pokedex.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.br.diegocunha.pokedex.R

private val defaultTypography = Typography()

val fontFamily = FontFamily(
    listOf(
        Font(R.font.circularstd_book),
        Font(R.font.circularstd_medium, FontWeight.W600),
        Font(R.font.circularstd_black, FontWeight.Bold),
        Font(R.font.circularstd_bold, FontWeight.W900)
    )
)
val Typography = Typography(
    h1 = defaultTypography.h1.copy(fontFamily = fontFamily),
    h2 = defaultTypography.h2.copy(fontFamily = fontFamily),
    h3 = defaultTypography.h3.copy(fontFamily = fontFamily),
    h4 = defaultTypography.h4.copy(fontFamily = fontFamily),
    h5 = defaultTypography.h5.copy(fontFamily = fontFamily),
    h6 = defaultTypography.h6.copy(fontFamily = fontFamily),
    subtitle1 = defaultTypography.subtitle1.copy(fontFamily = fontFamily),
    subtitle2 = defaultTypography.subtitle2.copy(fontFamily = fontFamily),
    body1 = defaultTypography.body1.copy(fontFamily = fontFamily),
    body2 = defaultTypography.body2.copy(fontFamily = fontFamily),
    button = defaultTypography.button.copy(fontFamily = fontFamily),
    caption = defaultTypography.caption.copy(fontFamily = fontFamily),
    overline = defaultTypography.overline.copy(fontFamily = fontFamily)
)