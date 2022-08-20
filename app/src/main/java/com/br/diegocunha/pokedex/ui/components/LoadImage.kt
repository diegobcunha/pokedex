package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

@Composable
fun LoadImage(
    imageResId: Int,
    tint: Color? = null,
    opacity: Float = 1.0f,
    rotation: Float = 0f
) {

    Image(
        modifier = Modifier.rotate(rotation),
        painter = painterResource(id = imageResId), contentDescription = "",
        colorFilter = tint?.let { color ->
            ColorFilter.tint(color.copy(alpha = opacity))
        }
    )
}