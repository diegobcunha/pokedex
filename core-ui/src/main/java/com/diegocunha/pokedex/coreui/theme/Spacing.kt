package com.diegocunha.pokedex.coreui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object PokedexSpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
}

val LocalPokedexSpacing = staticCompositionLocalOf { PokedexSpacing }

val MaterialTheme.spacing: PokedexSpacing
    @Composable @ReadOnlyComposable get() = LocalPokedexSpacing.current
