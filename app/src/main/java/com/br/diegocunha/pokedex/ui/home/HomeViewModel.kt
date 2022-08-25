package com.br.diegocunha.pokedex.ui.home

import android.os.Parcelable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.api.model.Sprites
import com.br.diegocunha.pokedex.datasource.api.model.Stats
import com.br.diegocunha.pokedex.datasource.api.model.Type
import com.br.diegocunha.pokedex.datasource.source.PokemonSource
import com.br.diegocunha.pokedex.extensions.transformPagingData
import com.br.diegocunha.pokedex.templates.PaginationViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

class HomeViewModel(
    dispatchersProvider: DispatchersProvider,
    private val source: PokemonSource
) : PaginationViewModel<PokemonUI>(dispatchersProvider, 1) {

    override fun fetchContent(initialPageSize: Int): Flow<PagingData<PokemonUI>> {
        return Pager(
            PagingConfig(
                pageSize = 1
            )
        ) { source }.flow.transformPagingData {
            PokemonUI(
                id = it.id,
                name = it.name,
                sprites = it.sprites,
                height = it.height,
                weight = it.weight,
                stats = it.stats,
                types = it.types
            )
        }
    }
}

@Parcelize
data class PokemonUI(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<Stats>,
    val height: Int,
    val weight: Int,
    val types: List<Type>
) : Parcelable