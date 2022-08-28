package com.br.diegocunha.pokedex.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import com.br.diegocunha.pokedex.templates.GetFailure
import com.br.diegocunha.pokedex.templates.GetInitial
import com.br.diegocunha.pokedex.templates.GetState
import com.br.diegocunha.pokedex.templates.GetStatus

@Composable
fun <T> GetCrossfade(
    state: GetState<T>,
    initial: @Composable (GetInitial) -> Unit,
    failure: @Composable (GetFailure) -> Unit,
    success: @Composable (T) -> Unit,
) {
    Crossfade(targetState = state) { targetState ->
        when (targetState.currentStatus()) {
            GetStatus.INITIAL -> initial(targetState.initial)
            GetStatus.FAILURE -> failure(targetState.failure)
            GetStatus.SUCCESS -> success(targetState.success)
        }
    }
}