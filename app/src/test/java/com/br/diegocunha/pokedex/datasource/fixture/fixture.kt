package com.br.diegocunha.pokedex.datasource.fixture

import com.br.diegocunha.pokedex.datasource.api.model.Pokemon
import com.br.diegocunha.pokedex.datasource.api.model.PokemonResponse
import com.br.diegocunha.pokedex.datasource.api.model.PokemonResult
import com.br.diegocunha.pokedex.datasource.api.model.SinglePokemonResult
import com.br.diegocunha.pokedex.datasource.api.model.Sprites

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
    Sprites(),
    emptyList(),
    10,
    10,
    emptyList()
)

val pokemon = Pokemon(
    1,
    "name",
    sprites = Sprites(),
    stats = emptyList(),
    10,
    10,
    emptyList()
)