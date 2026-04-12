package com.diegocunha.pokedex.datasource.repository

import app.cash.turbine.test
import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.dao.PokemonDetailDao
import com.diegocunha.pokedex.datasource.db.dao.PokemonEvolutionDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonDetailEntity
import com.diegocunha.pokedex.datasource.db.entity.PokemonEvolutionEntity
import com.diegocunha.pokedex.datasource.model.ChainLink
import com.diegocunha.pokedex.datasource.model.EvolutionChainRef
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.NamedResource
import com.diegocunha.pokedex.datasource.model.PokemonAbility
import com.diegocunha.pokedex.datasource.model.PokemonAbilitySlot
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.model.PokemonSpeciesResponse
import com.diegocunha.pokedex.datasource.model.PokemonSprites
import com.diegocunha.pokedex.datasource.model.PokemonStat
import com.diegocunha.pokedex.datasource.model.PokemonStatSlot
import com.diegocunha.pokedex.datasource.model.PokemonType
import com.diegocunha.pokedex.datasource.model.PokemonTypeSlot
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private val testDispatchers = object : DispatchersProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
}

private val testJson = Json { ignoreUnknownKeys = true }

class PokemonRepositoryImplTest {

    private val mockApiService = mockk<PokemonApiService>()
    private val mockDetailDao = mockk<PokemonDetailDao>(relaxed = true)
    private val mockEvolutionDao = mockk<PokemonEvolutionDao>(relaxed = true)

    private fun repository() = PokemonRepositoryImpl(
        mockApiService, testDispatchers, mockDetailDao, mockEvolutionDao, testJson
    )

    private fun fakePokemon(id: Int = 1) = PokemonResponse(
        id = id,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        types = listOf(PokemonTypeSlot(1, PokemonType("grass", "url"))),
        stats = listOf(PokemonStatSlot(45, 0, PokemonStat("hp", "url"))),
        sprites = PokemonSprites("front_url", null),
        abilities = listOf(PokemonAbilitySlot(PokemonAbility("overgrow", "url"), false, 1))
    )

    private fun fakeCachedEntity(lastFetched: Long = System.currentTimeMillis()) = PokemonDetailEntity(
        id = "1",
        name = "bulbasaur",
        height = 7,
        weight = 69,
        types = """["grass"]""",
        stats = """[{"name":"hp","baseStat":45}]""",
        abilities = """["overgrow"]""",
        imageUrl = "front_url",
        lastFetched = lastFetched
    )

    @Test
    fun `cache hit fresh - emits Loading then Success from cache, no network call`() = runTest {
        val freshEntity = fakeCachedEntity(lastFetched = System.currentTimeMillis())
        coEvery { mockDetailDao.getById("1") } returns freshEntity

        repository().getPokemonDetail(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals("bulbasaur", (success as Resource.Success).data.name)
            awaitComplete()
        }

        coVerify(exactly = 0) { mockApiService.getPokemonDetail(any()) }
    }

    @Test
    fun `cache miss - emits Loading then Success from network, saves to DB`() = runTest {
        val pokemon = fakePokemon()
        coEvery { mockDetailDao.getById("1") } returns null
        coEvery { mockApiService.getPokemonDetail(1) } returns pokemon

        repository().getPokemonDetail(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(pokemon, (success as Resource.Success).data)
            awaitComplete()
        }

        coVerify(exactly = 1) { mockDetailDao.insert(any()) }
    }

    @Test
    fun `cache hit stale data changed - emits Loading, cached Success, then network Success`() = runTest {
        val staleEntity = fakeCachedEntity(lastFetched = System.currentTimeMillis() - 25 * 60 * 60 * 1000L)
        val newPokemon = fakePokemon().copy(name = "ivysaur")
        coEvery { mockDetailDao.getById("1") } returns staleEntity
        coEvery { mockApiService.getPokemonDetail(1) } returns newPokemon

        repository().getPokemonDetail(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val cached = awaitItem()
            assertTrue(cached is Resource.Success)
            assertEquals("bulbasaur", (cached as Resource.Success).data.name)
            val updated = awaitItem()
            assertTrue(updated is Resource.Success)
            assertEquals("ivysaur", (updated as Resource.Success).data.name)
            awaitComplete()
        }
    }

    @Test
    fun `cache hit stale data same - emits Loading and cached Success, no second emission`() = runTest {
        val staleEntity = fakeCachedEntity(lastFetched = System.currentTimeMillis() - 25 * 60 * 60 * 1000L)
        val samePokemon = fakePokemon() // same data as cached
        coEvery { mockDetailDao.getById("1") } returns staleEntity
        coEvery { mockApiService.getPokemonDetail(1) } returns samePokemon

        repository().getPokemonDetail(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val cached = awaitItem()
            assertTrue(cached is Resource.Success)
            assertEquals("bulbasaur", (cached as Resource.Success).data.name)
            awaitComplete()
        }
    }

    @Test
    fun `network error no cache - emits Loading then Error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockDetailDao.getById("1") } returns null
        coEvery { mockApiService.getPokemonDetail(1) } throws exception

        repository().getPokemonDetail(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            awaitComplete()
        }
    }

    @Test
    fun `network error with cache hit - emits Loading then cached Success, no Error`() = runTest {
        val staleEntity = fakeCachedEntity(lastFetched = System.currentTimeMillis() - 25 * 60 * 60 * 1000L)
        coEvery { mockDetailDao.getById("1") } returns staleEntity
        coEvery { mockApiService.getPokemonDetail(1) } throws RuntimeException("Network error")

        repository().getPokemonDetail(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val cached = awaitItem()
            assertTrue(cached is Resource.Success)
            assertEquals("bulbasaur", (cached as Resource.Success).data.name)
            awaitComplete()
        }
    }

    // --- getEvolutionData tests ---

    @Test
    fun `evolution cache hit - emits Loading then Success from cache, no network calls`() = runTest {
        val chainJson = testJson.encodeToString(EvolutionChainResponse.serializer(), fakeChainResponse())
        coEvery { mockEvolutionDao.getByPokemonId("1") } returns PokemonEvolutionEntity("1", 1, chainJson)

        repository().getEvolutionData(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, (success as Resource.Success).data.id)
            awaitComplete()
        }

        coVerify(exactly = 0) { mockApiService.getPokemonSpecies(any()) }
        coVerify(exactly = 0) { mockApiService.getEvolutionChain(any()) }
    }

    @Test
    fun `evolution cache miss - fetches species then chain and caches result`() = runTest {
        coEvery { mockEvolutionDao.getByPokemonId("1") } returns null
        coEvery { mockApiService.getPokemonSpecies(1) } returns fakeSpeciesResponse(chainId = 1)
        coEvery { mockApiService.getEvolutionChain(1) } returns fakeChainResponse()

        repository().getEvolutionData(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, (success as Resource.Success).data.id)
            awaitComplete()
        }

        coVerify(exactly = 1) { mockApiService.getPokemonSpecies(1) }
        coVerify(exactly = 1) { mockApiService.getEvolutionChain(1) }
        coVerify(exactly = 1) { mockEvolutionDao.insert(any()) }
    }

    @Test
    fun `evolution species call fails - emits Loading then Error`() = runTest {
        coEvery { mockEvolutionDao.getByPokemonId("1") } returns null
        coEvery { mockApiService.getPokemonSpecies(1) } throws RuntimeException("Species error")

        repository().getEvolutionData(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }

        coVerify(exactly = 0) { mockApiService.getEvolutionChain(any()) }
    }

    @Test
    fun `evolution chain call fails - emits Loading then Error`() = runTest {
        coEvery { mockEvolutionDao.getByPokemonId("1") } returns null
        coEvery { mockApiService.getPokemonSpecies(1) } returns fakeSpeciesResponse(chainId = 1)
        coEvery { mockApiService.getEvolutionChain(1) } throws RuntimeException("Chain error")

        repository().getEvolutionData(1).test {
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }

        coVerify(exactly = 0) { mockEvolutionDao.insert(any()) }
    }

    private fun fakeSpeciesResponse(chainId: Int = 1) = PokemonSpeciesResponse(
        id = 1,
        evolutionChain = EvolutionChainRef(url = "https://pokeapi.co/api/v2/evolution-chain/$chainId/")
    )

    private fun fakeChainResponse() = EvolutionChainResponse(
        id = 1,
        chain = ChainLink(
            species = NamedResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/"),
            evolvesTo = emptyList()
        )
    )
}
