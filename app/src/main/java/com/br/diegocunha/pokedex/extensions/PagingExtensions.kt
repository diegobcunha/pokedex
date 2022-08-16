package com.br.diegocunha.pokedex.extensions

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T : Any, R : Any> Flow<PagingData<T>>.transformPagingData(crossinline transform: suspend (value: T) -> R) =
    map { pagingView ->
        pagingView.map {
            transform(it)
        }
    }