package com.diegocunha.pokedex.feature.pokemon.domain.mapper

import com.diegocunha.pokedex.datasource.model.PokemonAbility
import com.diegocunha.pokedex.datasource.model.PokemonAbilitySlot
import com.diegocunha.pokedex.datasource.model.PokemonEntryResponse
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.model.PokemonSprites
import com.diegocunha.pokedex.datasource.model.PokemonStat
import com.diegocunha.pokedex.datasource.model.PokemonStatSlot
import com.diegocunha.pokedex.datasource.model.PokemonType
import com.diegocunha.pokedex.datasource.model.PokemonTypeSlot
import com.diegocunha.pokedex.feature.pokemon.domain.model.PokemonEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PokemonMapperTest {

    // PokemonEntryResponse.toDomain()

    @Test
    fun `toDomain extracts id from url with trailing slash`() {
        val response = PokemonEntryResponse(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
        val result = response.toDomain()
        assertEquals(PokemonEntry(id = "1", name = "bulbasaur"), result)
    }

    @Test
    fun `toDomain extracts id from url without trailing slash`() {
        val response = PokemonEntryResponse(name = "charmander", url = "https://pokeapi.co/api/v2/pokemon/4")
        val result = response.toDomain()
        assertEquals(PokemonEntry(id = "4", name = "charmander"), result)
    }

    // PokemonResponse.toDomain()

    @Test
    fun `toDomain maps id as string`() {
        val result = fakePokemonResponse(id = 25).toDomain()
        assertEquals("25", result.id)
    }

    @Test
    fun `toDomain maps types to flat string list`() {
        val result = fakePokemonResponse().toDomain()
        assertEquals(listOf("grass", "poison"), result.types)
    }

    @Test
    fun `toDomain maps stats to domain stat list`() {
        val result = fakePokemonResponse().toDomain()
        assertEquals(1, result.stats.size)
        assertEquals("hp", result.stats[0].name)
        assertEquals(45, result.stats[0].value)
    }

    @Test
    fun `toDomain maps imageUrl from frontDefault`() {
        val result = fakePokemonResponse().toDomain()
        assertEquals("front_url", result.imageUrl)
    }

    @Test
    fun `toDomain maps null imageUrl when frontDefault is null`() {
        val result = fakePokemonResponse(frontDefault = null).toDomain()
        assertNull(result.imageUrl)
    }

    @Test
    fun `toDomain maps abilities to flat string list`() {
        val result = fakePokemonResponse().toDomain()
        assertEquals(listOf("overgrow"), result.abilities)
    }

    private fun fakePokemonResponse(
        id: Int = 1,
        frontDefault: String? = "front_url"
    ) = PokemonResponse(
        id = id,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        types = listOf(
            PokemonTypeSlot(1, PokemonType("grass", "url")),
            PokemonTypeSlot(2, PokemonType("poison", "url"))
        ),
        stats = listOf(PokemonStatSlot(45, 0, PokemonStat("hp", "url"))),
        sprites = PokemonSprites(frontDefault, null),
        abilities = listOf(PokemonAbilitySlot(PokemonAbility("overgrow", "url"), false, 1))
    )
}
