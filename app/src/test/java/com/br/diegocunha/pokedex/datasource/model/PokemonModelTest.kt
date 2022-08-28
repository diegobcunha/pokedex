package com.br.diegocunha.pokedex.datasource.model

import com.br.diegocunha.pokedex.datasource.fixture.pokemon
import com.br.diegocunha.pokedex.datasource.repository.toPokemonUI
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PokemonModelTest {

    @Test
    fun `GIVEN a Pokemon model THEN should convert correctly to PokemonUI`() {
        val pokemonModel = pokemon
        val pokemonUiModel = pokemonModel.toPokemonUI()

        assertEquals(pokemonModel.id, pokemonUiModel.id)
        assertEquals(pokemonModel.height, pokemonUiModel.height)
        assertEquals(pokemonModel.weight, pokemonUiModel.weight)
        assertEquals(pokemonModel.sprites, pokemonUiModel.sprites)
        assertEquals(pokemonModel.stats.size, pokemonUiModel.stats.size)
        assertEquals(pokemonModel.types.size, pokemonUiModel.types.size)
    }
}