package com.br.diegocunha.pokedex.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.br.diegocunha.pokedex.R
import com.br.diegocunha.pokedex.ui.theme.LightColorPalette
import com.br.diegocunha.pokedex.ui.theme.colorGrey100
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun PokeAppBar(
    onSearch: (String) -> Unit,
    content: @Composable () -> Unit
) {
    AppBar(background = { PokeBallBackground() }) {
        Column(
            Modifier.padding(
                top = 32.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }

    }
}

@Composable
private fun AppBar(
    background: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(
            bottomStart = 32.dp,
            bottomEnd = 32.dp
        )
    ) {
        Box {
            background()

            Box(contentAlignment = Alignment.TopCenter) {
                content()
            }
        }
    }
}

@Composable
fun DefaultTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = colorGrey100,
    contentColor: Color = LightColorPalette.onPrimary,
    statusBarColor: Color = backgroundColor,
    statusBar: @Composable () -> Unit = { StatusBar(color = statusBarColor) },
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    statusBar()
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
    )
}

@Composable
fun BackNavigationIcon() {
    val activity = (LocalContext.current as Activity)
    IconButton(
        onClick = {
            activity.onBackPressed()
        }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(id = R.string.resources_backstack_description)
        )
    }
}

@Composable
fun StatusBar(color: Color, darkIcons: Boolean = MaterialTheme.colors.isLight) {
    val uiController = rememberSystemUiController()
    SideEffect {
        uiController.setStatusBarColor(color, darkIcons)
    }
}

@Preview
@Composable
fun PokeAppBarPreview() {
    PokeAppBar(onSearch = {}, content = {})
}