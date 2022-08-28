package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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

@Composable
fun CircularImage(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String? = null,
    tint: ColorFilter?
) {
    Image(
        modifier = modifier
            .clip(CircleShape),
        painter = painter,
        contentDescription = contentDescription,
        colorFilter = tint
    )
}

@Composable
fun HelperIcon(
    painter: Painter,
    backgroundColor: Color,
    statusTint: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = statusTint
        )
    }
}