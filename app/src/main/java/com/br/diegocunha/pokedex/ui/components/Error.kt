package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import com.br.diegocunha.pokedex.R
import com.br.diegocunha.pokedex.ui.theme.Typography
import com.br.diegocunha.pokedex.ui.theme.colorRedPokeDex
import com.br.diegocunha.pokedex.ui.theme.colorWhite100

@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    message: String?,
    onRetry: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message ?: "",
            maxLines = 1,
            modifier = Modifier.weight(1f),
            color = colorRedPokeDex
        )

        onRetry?.let {
            OutlinedButton(onClick = onRetry, colors = ButtonDefaults.outlinedButtonColors()) {
                Text(
                    text = "Try again",
                    color = colorRedPokeDex,
                )
            }
        }

    }
}

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                HelperIcon(
                    painter = painterResource(id = R.drawable.ic_close),
                    backgroundColor = colorRedPokeDex,
                    statusTint = colorWhite100
                )
            }
            Text(
                text = stringResource(id = R.string.error_title),
                fontStyle = Typography.h1.fontStyle,
                color = Color.Black,
                fontSize = 32.sp,
            )
            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                Text(
                    text = stringResource(id = R.string.error_subtitle),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontStyle = Typography.subtitle1.fontStyle,
                )
            }

            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(
                backgroundColor = colorRedPokeDex,
            )) {
                Text(
                    text = stringResource(id = R.string.try_again),
                    color = colorWhite100
                )
            }
        }
    }
}

@Composable
fun makeError(
    modifier: Modifier = Modifier,
    loadState: LoadState,
    onRetry: () -> Unit,
) {
    val message = (loadState as LoadState.Error).error

    ErrorItem(
        modifier = modifier,
        message = message.message,
        onRetry = onRetry
    )
}

@Preview(group = "Error")
@Composable
private fun ErrorItemPreview() {
    ErrorItem(message = "Error") {

    }
}

@Preview(group = "Error")
@Composable
private fun ErrorViewPreview() {
    ErrorView() {

    }
}