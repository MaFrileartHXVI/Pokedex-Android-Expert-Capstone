package com.mafrilearth.pokedex.core.data.source.remote.network

import com.mafrilearth.pokedex.core.data.source.remote.response.ListPokemonResponse
import com.mafrilearth.pokedex.core.data.source.remote.response.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ListPokemonResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonResponse

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): com.mafrilearth.pokedex.core.data.source.remote.response.PokemonSpeciesResponse

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(
        @Path("id") id: Int
    ): com.mafrilearth.pokedex.core.data.source.remote.response.EvolutionChainResponse

    @GET("type/{type}")
    suspend fun getPokemonByType(
        @Path("type") type: String
    ): com.mafrilearth.pokedex.core.data.source.remote.response.TypeDetailResponse
}
