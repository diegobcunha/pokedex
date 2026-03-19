package com.diegocunha.pokedex.feature.pokemon.domain.mapper

import com.diegocunha.pokedex.datasource.db.entity.PokemonListEntryEntity
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

    @Test
    fun `toDomain maps name`() {
        val result = fakePokemonResponse().toDomain()
        assertEquals("bulbasaur", result.name)
    }

    @Test
    fun `toDomain maps height`() {
        val result = fakePokemonResponse().toDomain()
        assertEquals(7, result.height)
    }

    @Test
    fun `toDomain maps weight`() {
        val result = fakePokemonResponse().toDomain()
        assertEquals(69, result.weight)
    }

    // PokemonEntryResponse.toDomain(detail: PokemonResponse)

    @Test
    fun `toDomain with detail extracts id from url with trailing slash`() {
        val entry = PokemonEntryResponse(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
        val result = entry.toDomain(fakePokemonResponse())
        assertEquals("1", result.id)
    }

    @Test
    fun `toDomain with detail extracts id from url without trailing slash`() {
        val entry = PokemonEntryResponse(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1")
        val result = entry.toDomain(fakePokemonResponse())
        assertEquals("1", result.id)
    }

    @Test
    fun `toDomain with detail maps name`() {
        val entry = PokemonEntryResponse(name = "charmander", url = "https://pokeapi.co/api/v2/pokemon/4/")
        val result = entry.toDomain(fakePokemonResponse())
        assertEquals("charmander", result.name)
    }

    @Test
    fun `toDomain with detail maps imageUrl from response sprites`() {
        val entry = PokemonEntryResponse(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
        val result = entry.toDomain(fakePokemonResponse(frontDefault = "https://img.url/1.png"))
        assertEquals("https://img.url/1.png", result.imageUrl)
    }

    @Test
    fun `toDomain with detail maps null imageUrl when sprites frontDefault is null`() {
        val entry = PokemonEntryResponse(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
        val result = entry.toDomain(fakePokemonResponse(frontDefault = null))
        assertNull(result.imageUrl)
    }

    @Test
    fun `toDomain with detail maps types sorted by slot`() {
        val response = fakePokemonResponse().copy(
            types = listOf(
                PokemonTypeSlot(slot = 2, type = PokemonType("poison", "url")),
                PokemonTypeSlot(slot = 1, type = PokemonType("grass", "url"))
            )
        )
        val entry = PokemonEntryResponse(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
        val result = entry.toDomain(response)
        assertEquals(listOf("grass", "poison"), result.types)
    }

    // PokemonListEntryEntity.toDomain()

    @Test
    fun `entity toDomain maps id`() {
        val entity = PokemonListEntryEntity(id = "42", name = "mewtwo", imageUrl = null, types = "[]")
        assertEquals("42", entity.toDomain().id)
    }

    @Test
    fun `entity toDomain maps name`() {
        val entity = PokemonListEntryEntity(id = "1", name = "bulbasaur", imageUrl = null, types = "[]")
        assertEquals("bulbasaur", entity.toDomain().name)
    }

    @Test
    fun `entity toDomain maps imageUrl`() {
        val entity = PokemonListEntryEntity(id = "1", name = "bulbasaur", imageUrl = "https://img.url/1.png", types = "[]")
        assertEquals("https://img.url/1.png", entity.toDomain().imageUrl)
    }

    @Test
    fun `entity toDomain maps null imageUrl`() {
        val entity = PokemonListEntryEntity(id = "1", name = "bulbasaur", imageUrl = null, types = "[]")
        assertNull(entity.toDomain().imageUrl)
    }

    @Test
    fun `entity toDomain decodes types from json`() {
        val entity = PokemonListEntryEntity(id = "1", name = "bulbasaur", imageUrl = null, types = """["grass","poison"]""")
        assertEquals(listOf("grass", "poison"), entity.toDomain().types)
    }

    @Test
    fun `entity toDomain decodes empty types list`() {
        val entity = PokemonListEntryEntity(id = "1", name = "bulbasaur", imageUrl = null, types = "[]")
        assertEquals(emptyList<String>(), entity.toDomain().types)
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
