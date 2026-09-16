package com.mafrilearth.pokedex.core.data

import com.mafrilearth.pokedex.core.data.source.local.LocalDataSource
import com.mafrilearth.pokedex.core.data.source.remote.RemoteDataSource
import com.mafrilearth.pokedex.core.data.source.remote.network.ApiResponse
import com.mafrilearth.pokedex.core.data.source.remote.response.PokemonResultResponse
import com.mafrilearth.pokedex.core.data.source.remote.response.PokemonResponse
import com.mafrilearth.pokedex.core.domain.model.Pokemon
import com.mafrilearth.pokedex.core.domain.repository.IPokemonRepository
import com.mafrilearth.pokedex.core.domain.Resource
import com.mafrilearth.pokedex.core.utils.AppExecutors
import com.mafrilearth.pokedex.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : IPokemonRepository {

    override fun getAllPokemon(): Flow<Resource<List<Pokemon>>> =
        object : NetworkBoundResource<List<Pokemon>, List<PokemonResultResponse>>() {
            override fun loadFromDB(): Flow<List<Pokemon>> {
                return localDataSource.getAllPokemon().map { DataMapper.mapEntitiesToDomain(it) }
            }

            override fun shouldFetch(data: List<Pokemon>?): Boolean =
                data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<PokemonResultResponse>>> =
                remoteDataSource.getAllPokemon()

            override suspend fun saveCallResult(data: List<PokemonResultResponse>) {
                val pokemonList = DataMapper.mapResponsesToEntities(data).map {
                    val currentEntity = localDataSource.getPokemonDetailSync(it.pokemonId)
                    it.copy(
                        isFavorite = currentEntity?.isFavorite ?: false,
                        isBaseEvolution = true
                    )
                }
                localDataSource.insertPokemon(pokemonList)
            }
        }.asFlow()

    override fun getPokemonByType(type: String): Flow<Resource<List<Pokemon>>> =
        object : NetworkBoundResource<List<Pokemon>, List<PokemonResultResponse>>() {
            override fun loadFromDB(): Flow<List<Pokemon>> {
                return localDataSource.getPokemonByType(type).map { DataMapper.mapEntitiesToDomain(it) }
            }

            @Suppress("SameReturnValue")
            override fun shouldFetch(data: List<Pokemon>?): Boolean =
                true // Always fetch from remote to get the list of this type

            override suspend fun createCall(): Flow<ApiResponse<List<PokemonResultResponse>>> =
                remoteDataSource.getPokemonByType(type)

            override suspend fun saveCallResult(data: List<PokemonResultResponse>) {
                val pokemonList = DataMapper.mapResponsesToEntities(data).map {
                    val currentEntity = localDataSource.getPokemonDetailSync(it.pokemonId)
                    it.copy(
                        types = listOf(type),
                        isFavorite = currentEntity?.isFavorite ?: false,
                        isBaseEvolution = currentEntity?.isBaseEvolution ?: false
                    )
                }
                localDataSource.insertPokemon(pokemonList)
            }
        }.asFlow()

    override fun getFavoritePokemon(): Flow<List<Pokemon>> {
        return localDataSource.getFavoritePokemon().map { DataMapper.mapEntitiesToDomain(it) }
    }

    override fun setFavoritePokemon(pokemon: Pokemon, state: Boolean) {
        appExecutors.diskIO().execute {
            kotlinx.coroutines.runBlocking {
                val pokemonEntity = DataMapper.mapDomainToEntity(pokemon)
                val currentEntity = localDataSource.getPokemonDetailSync(pokemon.id)
                val updatedEntity = currentEntity?.copy(isFavorite = state) ?: pokemonEntity.copy(isFavorite = state)
                localDataSource.setFavoritePokemon(updatedEntity, state)
            }
        }
    }

    override fun getPokemonDetail(id: Int): Flow<Resource<Pokemon>> =
        object : NetworkBoundResource<Pokemon, PokemonResponse>() {
            override fun loadFromDB(): Flow<Pokemon> {
                return localDataSource.getPokemonDetail(id).map {
                    if (it != null) DataMapper.mapEntitiesToDomain(listOf(it)).first()
                    else Pokemon(0, "", "") // Dummy fallback
                }
            }

            override fun shouldFetch(data: Pokemon?): Boolean =
                data == null || data.height == 0 || data.weight == 0 || data.evolutionChain.any { !it.contains("|") } // Fetch if detail not available or evolution chain is old format

            override suspend fun createCall(): Flow<ApiResponse<PokemonResponse>> =
                remoteDataSource.getPokemonDetail(id)

            override suspend fun saveCallResult(data: PokemonResponse) {
                val currentEntity = localDataSource.getPokemonDetailSync(data.id)
                val isFavorite = currentEntity?.isFavorite ?: false
                var description = ""
                var evolutionChainUrls = emptyList<String>()
                
                remoteDataSource.getPokemonSpecies(data.id).collect { speciesApiResponse ->
                    if (speciesApiResponse is ApiResponse.Success) {
                        val speciesResponse = speciesApiResponse.data
                        val enFlavor = speciesResponse.flavorTextEntries?.firstOrNull { it.language.name == "en" }
                        description = enFlavor?.flavorText?.replace("\n", " ")?.replace("\u000c", " ") ?: ""
                        val evolutionUrl = speciesResponse.evolutionChain?.url
                        val evolutionId = evolutionUrl?.split("/")?.dropLast(1)?.lastOrNull()?.toIntOrNull()
                        
                        if (evolutionId != null) {
                            remoteDataSource.getEvolutionChain(evolutionId).collect { evoApiResponse ->
                                if (evoApiResponse is ApiResponse.Success) {
                                    val evoResponse = evoApiResponse.data
                                    val chain = mutableListOf<String>()
                                    
                                    fun traverseChain(link: com.mafrilearth.pokedex.core.data.source.remote.response.ChainLinkResponse) {
                                        val evoEvoId = link.species.url.split("/").dropLast(1).lastOrNull() ?: "0"
                                        val evoImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$evoEvoId.png"
                                        chain.add("${link.species.name}|$evoImageUrl")
                                        if (link.evolvesTo.isNotEmpty()) {
                                            traverseChain(link.evolvesTo[0]) // just take the first path for simplicity
                                        }
                                    }
                                    
                                    traverseChain(evoResponse.chain)
                                    evolutionChainUrls = chain
                                }
                            }
                        }
                    }
                }
                val pokemonEntity = DataMapper.mapDetailResponseToEntity(data, isFavorite).copy(
                    description = description,
                    evolutionChain = evolutionChainUrls,
                    isBaseEvolution = currentEntity?.isBaseEvolution ?: false
                )
                localDataSource.insertPokemon(listOf(pokemonEntity))
            }
        }.asFlow()
}
