package com.br.diegocunha.pokedex.datasource.templates

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Source to get source and load pagination content
 * @param T Content which will be returned to requester, should be extended from Page to
 * get page key
 */
abstract class BasePaginationSource<T : Any> : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition
    }
}