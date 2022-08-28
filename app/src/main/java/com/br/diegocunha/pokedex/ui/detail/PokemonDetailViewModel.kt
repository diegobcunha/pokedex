package com.br.diegocunha.pokedex.ui.detail

import com.br.diegocunha.pokedex.coroutine.DispatchersProvider
import com.br.diegocunha.pokedex.datasource.repository.PokemonRepository
import com.br.diegocunha.pokedex.templates.GetState
import com.br.diegocunha.pokedex.templates.StateViewModel
import com.br.diegocunha.pokedex.templates.toGetState
import com.br.diegocunha.pokedex.ui.model.PokemonUI
import com.br.diegocunha.pokedex.ui.model.toPokemonUI

class PokemonDetailViewModel(
    dispatchersProvider: DispatchersProvider,
    private val pokemonRepository: PokemonRepository,
    private val pokemonId: Int

) : StateViewModel<PokemonUI>(dispatchersProvider) {

    override suspend fun fetch(): GetState<PokemonUI> {
        return pokemonRepository.getPokemonDetail(pokemonId).map {
            it.toPokemonUI()
        }.toGetState()
    }
}