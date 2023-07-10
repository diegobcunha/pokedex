package com.br.diegocunha.pokedex.datasource.fixture

import com.br.diegocunha.pokedex.datasource.api.model.PokemonResponse
import com.br.diegocunha.pokedex.datasource.api.model.PokemonResult
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import com.br.diegocunha.pokedex.datasource.core.Sprites
import com.br.diegocunha.pokedex.ui.model.toPokemonUI

val pokemonEmptyResponse = PokemonResponse(
    0,
    "offset=10&limit=10",
    "offset=0&limit=10",
    emptyList()
)

val pokemonResponse = pokemonEmptyResponse.copy(
    results = listOf(pokemonResult())
)

fun pokemonResult() = PokemonResult(
    "name",
    "url"
)

val singlePokemonResult = SinglePokemonResult(
    1,
    "name",
    Sprites(),
    emptyList(),
    10,
    10,
    emptyList(),
    emptyList()
)

val pokemon = SinglePokemonResult(
    1,
    "name",
    sprites = Sprites(),
    stats = emptyList(),
    10,
    10,
    emptyList(),
    emptyList()
)

val pokemonUi = pokemon.toPokemonUI()