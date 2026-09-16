package com.mafrilearth.pokedex.core.data.source.remote

import android.util.Log
import com.mafrilearth.pokedex.core.data.source.remote.network.ApiResponse
import com.mafrilearth.pokedex.core.data.source.remote.network.ApiService
import com.mafrilearth.pokedex.core.data.source.remote.response.PokemonResponse
import com.mafrilearth.pokedex.core.data.source.remote.response.PokemonResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RemoteDataSource(private val apiService: ApiService) {
    fun getAllPokemon(): Flow<ApiResponse<List<PokemonResultResponse>>> {
        return flow {
            try {
                val response = apiService.getPokemonList()
                val dataArray = response.results
                if (dataArray.isNotEmpty()) {
                    val filteredList = coroutineScope {
                        dataArray.map { pokemon ->
                            async {
                                try {
                                    val urlParts = pokemon.url.split("/")
                                    val id = urlParts[urlParts.size - 2].toInt()
                                    val species = apiService.getPokemonSpecies(id)
                                    if (species.evolvesFromSpecies == null) pokemon else null
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }.awaitAll().filterNotNull()
                    }
                    if (filteredList.isNotEmpty()) {
                        emit(ApiResponse.Success(filteredList))
                    } else {
                        emit(ApiResponse.Empty)
                    }
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPokemonDetail(id: Int): Flow<ApiResponse<PokemonResponse>> {
        return flow {
            try {
                val response = apiService.getPokemonDetail(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPokemonSpecies(id: Int): Flow<ApiResponse<com.mafrilearth.pokedex.core.data.source.remote.response.PokemonSpeciesResponse>> {
        return flow {
            try {
                val response = apiService.getPokemonSpecies(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getEvolutionChain(id: Int): Flow<ApiResponse<com.mafrilearth.pokedex.core.data.source.remote.response.EvolutionChainResponse>> {
        return flow {
            try {
                val response = apiService.getEvolutionChain(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPokemonByType(type: String): Flow<ApiResponse<List<PokemonResultResponse>>> {
        return flow {
            try {
                val response = apiService.getPokemonByType(type.lowercase())
                val dataArray = response.pokemon.map { it.pokemon }
                if (dataArray.isNotEmpty()) {
                    emit(ApiResponse.Success(dataArray))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }
}
