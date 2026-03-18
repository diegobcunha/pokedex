package com.diegocunha.pokedex.coreui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PokedexRedDark,
    primaryContainer = PokedexRedDarkContainer,
    secondary = PokedexNavyDark,
    secondaryContainer = PokedexNavyDarkContainer,
    tertiary = PokedexYellowDark,
    background = PokedexBackgroundDark,
    surface = PokedexSurfaceDark,
    onPrimary = PokedexOnPrimaryDark,
    onBackground = PokedexOnBackgroundDark,
    onSurface = PokedexOnBackgroundDark,
    surfaceVariant = PokedexSurfaceVariantDark,
    onSurfaceVariant = PokedexOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = PokedexRed,
    primaryContainer = PokedexRedLight,
    secondary = PokedexNavy,
    secondaryContainer = PokedexNavyLight,
    tertiary = PokedexYellow,
    background = PokedexBackground,
    surface = PokedexSurface,
    onPrimary = PokedexOnPrimary,
    onBackground = PokedexOnBackground,
    onSurface = PokedexOnSurface,
    surfaceVariant = PokedexSurfaceVariant,
    onSurfaceVariant = PokedexOnSurfaceVariant
)

@Composable
fun PokedexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalPokedexSpacing provides PokedexSpacing) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = PokedexShapes,
            content = content
        )
    }
}
