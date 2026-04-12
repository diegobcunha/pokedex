package com.diegocunha.pokedex.feature.pokemon.domain.mapper

import com.diegocunha.pokedex.datasource.model.ChainLink
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.EvolutionDetail
import com.diegocunha.pokedex.datasource.model.NamedResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EvolutionMapperTest {

    // --- Single-stage (no evolutions) ---

    @Test
    fun `single stage pokemon maps with empty evolvesTo`() {
        val response = chainResponse(
            chain = chainLink("mewtwo", "150", evolvesTo = emptyList())
        )
        val result = response.toDomain()
        assertEquals("150", result.base.pokemonId)
        assertEquals("mewtwo", result.base.pokemonName)
        assertNull(result.base.trigger)
        assertTrue(result.base.evolvesTo.isEmpty())
    }

    @Test
    fun `single stage maps image url from species id`() {
        val response = chainResponse(chain = chainLink("mewtwo", "150"))
        val result = response.toDomain()
        assertEquals(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png",
            result.base.imageUrl
        )
    }

    // --- Linear chain (A → B → C) ---

    @Test
    fun `linear chain maps three stages correctly`() {
        val response = chainResponse(
            chain = chainLink(
                "charmander", "4",
                evolvesTo = listOf(
                    chainLink(
                        "charmeleon", "5",
                        trigger = levelTrigger(16),
                        evolvesTo = listOf(
                            chainLink("charizard", "6", trigger = levelTrigger(36))
                        )
                    )
                )
            )
        )
        val result = response.toDomain()
        assertEquals("4", result.base.pokemonId)
        assertNull(result.base.trigger)

        val stage2 = result.base.evolvesTo.single()
        assertEquals("5", stage2.pokemonId)
        assertEquals("Level 16", stage2.trigger)

        val stage3 = stage2.evolvesTo.single()
        assertEquals("6", stage3.pokemonId)
        assertEquals("Level 36", stage3.trigger)
        assertTrue(stage3.evolvesTo.isEmpty())
    }

    // --- Branching chain (Eevee-style) ---

    @Test
    fun `branching chain maps all branches`() {
        val response = chainResponse(
            chain = chainLink(
                "eevee", "133",
                evolvesTo = listOf(
                    chainLink("vaporeon", "134", trigger = itemTrigger("water-stone")),
                    chainLink("jolteon", "135", trigger = itemTrigger("thunder-stone")),
                    chainLink("flareon", "136", trigger = itemTrigger("fire-stone"))
                )
            )
        )
        val result = response.toDomain()
        assertEquals("133", result.base.pokemonId)
        assertEquals(3, result.base.evolvesTo.size)

        val vaporeon = result.base.evolvesTo.first { it.pokemonId == "134" }
        assertEquals("Use Water Stone", vaporeon.trigger)

        val jolteon = result.base.evolvesTo.first { it.pokemonId == "135" }
        assertEquals("Use Thunder Stone", jolteon.trigger)

        val flareon = result.base.evolvesTo.first { it.pokemonId == "136" }
        assertEquals("Use Fire Stone", flareon.trigger)
    }

    // --- Trigger label priority ---

    @Test
    fun `trigger label - level up uses min level`() {
        val detail = EvolutionDetail(
            trigger = NamedResource("level-up", ""),
            minLevel = 20
        )
        val response = chainResponseWithDetail("ivysaur", "2", detail)
        val result = response.toDomain()
        assertEquals("Level 20", result.base.evolvesTo.first().trigger)
    }

    @Test
    fun `trigger label - use-item uses item name`() {
        val detail = EvolutionDetail(
            trigger = NamedResource("use-item", ""),
            item = NamedResource("fire-stone", "")
        )
        val response = chainResponseWithDetail("flareon", "136", detail)
        val result = response.toDomain()
        assertEquals("Use Fire Stone", result.base.evolvesTo.first().trigger)
    }

    @Test
    fun `trigger label - held item`() {
        val detail = EvolutionDetail(
            trigger = NamedResource("level-up", ""),
            heldItem = NamedResource("metal-coat", "")
        )
        val response = chainResponseWithDetail("steelix", "208", detail)
        val result = response.toDomain()
        assertEquals("Hold Metal Coat", result.base.evolvesTo.first().trigger)
    }

    @Test
    fun `trigger label - high friendship`() {
        val detail = EvolutionDetail(
            trigger = NamedResource("level-up", ""),
            minHappiness = 220
        )
        val response = chainResponseWithDetail("togetic", "176", detail)
        val result = response.toDomain()
        assertEquals("High Friendship", result.base.evolvesTo.first().trigger)
    }

    @Test
    fun `trigger label - day time`() {
        val detail = EvolutionDetail(
            trigger = NamedResource("level-up", ""),
            timeOfDay = "day"
        )
        val response = chainResponseWithDetail("espeon", "196", detail)
        val result = response.toDomain()
        assertEquals("Daytime", result.base.evolvesTo.first().trigger)
    }

    @Test
    fun `trigger label - night time`() {
        val detail = EvolutionDetail(
            trigger = NamedResource("level-up", ""),
            timeOfDay = "night"
        )
        val response = chainResponseWithDetail("umbreon", "197", detail)
        val result = response.toDomain()
        assertEquals("Nighttime", result.base.evolvesTo.first().trigger)
    }

    @Test
    fun `trigger label - fallback to formatted trigger name`() {
        val detail = EvolutionDetail(trigger = NamedResource("level-up", ""))
        val response = chainResponseWithDetail("something", "999", detail)
        val result = response.toDomain()
        assertEquals("Level Up", result.base.evolvesTo.first().trigger)
    }

    @Test
    fun `trigger label - level takes priority over held item`() {
        val detail = EvolutionDetail(
            trigger = NamedResource("level-up", ""),
            minLevel = 25,
            heldItem = NamedResource("metal-coat", "")
        )
        val response = chainResponseWithDetail("steelix", "208", detail)
        val result = response.toDomain()
        assertEquals("Level 25", result.base.evolvesTo.first().trigger)
    }

    // --- Helpers ---

    private fun chainResponse(chain: ChainLink) = EvolutionChainResponse(id = 1, chain = chain)

    private fun chainResponseWithDetail(name: String, speciesId: String, detail: EvolutionDetail) =
        chainResponse(
            chain = chainLink(
                "base", "1",
                evolvesTo = listOf(chainLink(name, speciesId, trigger = detail))
            )
        )

    private fun chainLink(
        name: String,
        speciesId: String,
        trigger: EvolutionDetail? = null,
        evolvesTo: List<ChainLink> = emptyList()
    ) = ChainLink(
        species = NamedResource(name, "https://pokeapi.co/api/v2/pokemon-species/$speciesId/"),
        evolutionDetails = if (trigger != null) listOf(trigger) else emptyList(),
        evolvesTo = evolvesTo
    )

    private fun levelTrigger(level: Int) = EvolutionDetail(
        trigger = NamedResource("level-up", ""),
        minLevel = level
    )

    private fun itemTrigger(itemName: String) = EvolutionDetail(
        trigger = NamedResource("use-item", ""),
        item = NamedResource(itemName, "")
    )
}
