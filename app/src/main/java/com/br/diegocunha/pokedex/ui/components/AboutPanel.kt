package com.br.diegocunha.pokedex.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AboutPanelLayout(
    modifier: Modifier = Modifier,
    startSlot: @Composable () -> Unit,
    middleSlot: @Composable () -> Unit,
    endSlot: @Composable () -> Unit,
    dividerColor: Color = Color.LightGray
) {
    Row(
        modifier = modifier
            .height(52.dp)
    ) {
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            startSlot.invoke()
        }
        DividerVertical(color = dividerColor)
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            middleSlot.invoke()
        }
        DividerVertical(color = dividerColor)
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier, contentAlignment = Alignment.TopEnd) {
            endSlot.invoke()
        }

    }
}

@Composable
fun AboutPanelIconInfo(
    modifier: Modifier = Modifier,
    iconLabelPainter: Painter,
    textLabel: String,
    informationTitle: String,
    iconColor: Color
) {
    AboutPanelInfo(information = {
        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
            Image(
                modifier = Modifier.size(16.dp),
                painter = iconLabelPainter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = iconColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = textLabel,
                style = MaterialTheme.typography.body1,
                color = Color.Black
            )
        }
    }, informationTitle = informationTitle)
}

@Composable
fun AboutPanelListInfo(
    modifier: Modifier = Modifier,
    infoList: List<String>,
    informationTitle: String,
    textColor: Color = Color.Black
) {
    AboutPanelInfo(information = {
        Column(modifier = modifier) {
            infoList.forEach { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1,
                    color = textColor
                )
            }
        }
    }, informationTitle = informationTitle)
}

@Composable
fun AboutPanelInfo(information: @Composable () -> Unit, informationTitle: String) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(74.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        information.invoke()
        Text(
            modifier = Modifier
                .weight(1f, false)
                .fillMaxWidth(),
            text = informationTitle,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = Color.Black,
        )
    }
}