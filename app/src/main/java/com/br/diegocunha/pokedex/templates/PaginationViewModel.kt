package com.br.diegocunha.pokedex.templates

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import kotlinx.coroutines.flow.Flow

/**
 * A Pagination ViewModel to handle Pagination with Jetpack Compose
 * @param dispatchersProvider Used at CoroutinesViewModel
 * @param T object which will be returned to View
 * @param initialLoadSize Size of load which will be used at start of screen
 */
abstract class PaginationViewModel<T : Any>(
    dispatchersProvider: DispatchersProvider,
    initialLoadSize: Int
) : CoroutineViewModel(dispatchersProvider) {

    /**
     * Flow to emit values for Views
     */
    private val _paginationFlow: Flow<PagingData<T>> by lazy {
        fetchContent(initialLoadSize).cachedIn(viewModelScope)
    }

    /**
     * Visible flow to be used at Views
     */
    val pagingFlow: Flow<PagingData<T>> by lazy { _paginationFlow }

    /**
     * Function which will implement pagination network call
     */
    abstract fun fetchContent(initialPageSize: Int): Flow<PagingData<T>>
}