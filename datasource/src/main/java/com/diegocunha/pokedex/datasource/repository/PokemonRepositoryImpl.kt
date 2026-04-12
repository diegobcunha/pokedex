package com.diegocunha.pokedex.datasource.repository

import com.diegocunha.pokedex.core.Resource
import com.diegocunha.pokedex.core.coroutines.DispatchersProvider
import com.diegocunha.pokedex.datasource.db.dao.PokemonDetailDao
import com.diegocunha.pokedex.datasource.db.dao.PokemonEvolutionDao
import com.diegocunha.pokedex.datasource.db.entity.PokemonDetailEntity
import com.diegocunha.pokedex.datasource.db.entity.PokemonEvolutionEntity
import com.diegocunha.pokedex.datasource.model.EvolutionChainResponse
import com.diegocunha.pokedex.datasource.model.PokemonAbility
import com.diegocunha.pokedex.datasource.model.PokemonAbilitySlot
import com.diegocunha.pokedex.datasource.model.PokemonResponse
import com.diegocunha.pokedex.datasource.model.PokemonSprites
import com.diegocunha.pokedex.datasource.model.PokemonStat
import com.diegocunha.pokedex.datasource.model.PokemonStatSlot
import com.diegocunha.pokedex.datasource.model.PokemonType
import com.diegocunha.pokedex.datasource.model.PokemonTypeSlot
import com.diegocunha.pokedex.datasource.network.PokemonApiService
import com.diegocunha.pokedex.datasource.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val TTL_MS = 24 * 60 * 60 * 1000L

@Serializable
private data class CachedStat(val name: String, val baseStat: Int)

class PokemonRepositoryImpl(
    private val apiService: PokemonApiService,
    private val dispatchersProvider: DispatchersProvider,
    private val pokemonDetailDao: PokemonDetailDao,
    private val pokemonEvolutionDao: PokemonEvolutionDao,
    private val json: Json,
) : PokemonRepository {

    override fun getPokemonDetail(id: Int): Flow<Resource<PokemonResponse>> = flow {
        emit(Resource.Loading)

        val cached = pokemonDetailDao.getById(id.toString())

        if (cached != null) {
            emit(Resource.Success(cached.toPokemonResponse()))
            if (System.currentTimeMillis() - cached.lastFetched < TTL_MS) return@flow
        }

        when (val result = safeApiCall(dispatchersProvider) { apiService.getPokemonDetail(id) }) {
            is Resource.Success -> {
                val newEntity = result.data.toDetailEntity(System.currentTimeMillis())
                pokemonDetailDao.insert(newEntity)
                if (cached == null || newEntity != cached.copy(lastFetched = newEntity.lastFetched)) {
                    emit(Resource.Success(result.data))
                }
            }
            is Resource.Error -> {
                if (cached == null) emit(Resource.Error(result.exception))
            }
            is Resource.Loading -> Unit
        }
    }

    override suspend fun getEvolutionChain(id: Int): Resource<EvolutionChainResponse> =
        safeApiCall(dispatchersProvider) { apiService.getEvolutionChain(id) }

    override fun getEvolutionData(pokemonId: Int): Flow<Resource<EvolutionChainResponse>> = flow {
        emit(Resource.Loading)

        val cached = pokemonEvolutionDao.getByPokemonId(pokemonId.toString())
        if (cached != null) {
            emit(Resource.Success(json.decodeFromString(EvolutionChainResponse.serializer(), cached.chainJson)))
            return@flow
        }

        val speciesResult = safeApiCall(dispatchersProvider) { apiService.getPokemonSpecies(pokemonId) }
        if (speciesResult is Resource.Error) {
            emit(Resource.Error(speciesResult.exception))
            return@flow
        }
        val chainId = extractIdFromUrl((speciesResult as Resource.Success).data.evolutionChain.url)

        val chainResult = safeApiCall(dispatchersProvider) { apiService.getEvolutionChain(chainId) }
        if (chainResult is Resource.Error) {
            emit(Resource.Error(chainResult.exception))
            return@flow
        }
        val chain = (chainResult as Resource.Success).data
        pokemonEvolutionDao.insert(
            PokemonEvolutionEntity(
                pokemonId = pokemonId.toString(),
                chainId = chainId,
                chainJson = json.encodeToString(EvolutionChainResponse.serializer(), chain)
            )
        )
        emit(Resource.Success(chain))
    }

    private fun extractIdFromUrl(url: String): Int =
        url.trimEnd('/').substringAfterLast('/').toInt()

    private fun PokemonResponse.toDetailEntity(lastFetched: Long) = PokemonDetailEntity(
        id = id.toString(),
        name = name,
        height = height,
        weight = weight,
        types = json.encodeToString(
            ListSerializer(String.serializer()),
            types.sortedBy { it.slot }.map { it.type.name }
        ),
        stats = json.encodeToString(
            ListSerializer(CachedStat.serializer()),
            stats.map { CachedStat(it.stat.name, it.baseStat) }
        ),
        abilities = json.encodeToString(
            ListSerializer(String.serializer()),
            abilities.map { it.ability.name }
        ),
        imageUrl = sprites.frontDefault,
        lastFetched = lastFetched
    )

    private fun PokemonDetailEntity.toPokemonResponse(): PokemonResponse {
        val typeNames = json.decodeFromString(ListSerializer(String.serializer()), types)
        val statEntries = json.decodeFromString(ListSerializer(CachedStat.serializer()), stats)
        val abilityNames = json.decodeFromString(ListSerializer(String.serializer()), abilities)
        return PokemonResponse(
            id = id.toInt(),
            name = name,
            height = height,
            weight = weight,
            types = typeNames.mapIndexed { i, t -> PokemonTypeSlot(i + 1, PokemonType(t, "")) },
            stats = statEntries.map { PokemonStatSlot(it.baseStat, 0, PokemonStat(it.name, "")) },
            sprites = PokemonSprites(imageUrl, null),
            abilities = abilityNames.map { PokemonAbilitySlot(PokemonAbility(it, ""), false, 0) }
        )
    }
}
