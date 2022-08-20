package com.br.diegocunha.pokedex.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.diegocunha.pokedex.R


@Composable
fun PokeBallLarge(tint: Color, opacity: Float = 1f, rotation: Float = 0f) {
    PokeBall(tint, opacity, R.drawable.pokeball)
}

@Composable
fun PokeBallSmall(tint: Color, opacity: Float = 1f) {
    PokeBall(tint, opacity, R.drawable.pokeball_s)
}

@Composable
fun PokeBallRotation(
    degree: Float = 0f,
    children: @Composable (Float) -> Unit
) {
    var currentRotation by remember {
        mutableStateOf(degree)
    }

    val rotation = remember {
        Animatable(currentRotation)
    }

    LaunchedEffect(true) {
        rotation.animateTo(
            targetValue = currentRotation + 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ) {
            currentRotation = value
        }
    }
    children(rotation.value)
}

@Composable
private fun PokeBall(tint: Color, opacity: Float, imageResId: Int, rotation: Float = 0f) {
    LoadImage(imageResId, tint, opacity, rotation)
}

@Preview
@Composable
fun PreviewPokeBall() {
    Box(
        Modifier.size(100.dp)
    ) {
        PokeBall(tint = Color.Black, opacity = 1f, imageResId = R.drawable.pokeball)
    }
}