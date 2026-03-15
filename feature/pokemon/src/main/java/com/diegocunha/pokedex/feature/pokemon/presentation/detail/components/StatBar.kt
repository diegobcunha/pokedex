package com.diegocunha.pokedex.feature.pokemon.presentation.detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonStat

private const val STAT_MAX = 300

@Composable
fun StatBar(stat: PokemonStat, modifier: Modifier = Modifier) {
    val color = statColor(stat.name)
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statAbbreviation(stat.name),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(48.dp)
        )
        LinearProgressIndicator(
            progress = { (stat.value / STAT_MAX.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${stat.value}/$STAT_MAX",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun statAbbreviation(name: String): String = when (name.lowercase()) {
    "hp" -> "HP"
    "attack" -> "ATK"
    "defense" -> "DEF"
    "speed" -> "SPD"
    "special-attack" -> "SATK"
    "special-defense" -> "SDEF"
    else -> name.uppercase()
}

private fun statColor(name: String): Color = when (name.lowercase()) {
    "hp" -> Color(0xFFE53935)
    "attack" -> Color(0xFFFF9800)
    "defense" -> Color(0xFF1E88E5)
    "speed" -> Color(0xFF29B6F6)
    "special-attack" -> Color(0xFF43A047)
    "special-defense" -> Color(0xFF00897B)
    else -> Color(0xFF9E9E9E)
}
