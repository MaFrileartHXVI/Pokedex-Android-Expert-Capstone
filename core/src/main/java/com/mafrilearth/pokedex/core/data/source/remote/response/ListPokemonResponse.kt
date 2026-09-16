package com.mafrilearth.pokedex.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListPokemonResponse(
    @field:SerializedName("results")
    val results: List<PokemonResultResponse>
)

data class PokemonResultResponse(
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("url")
    val url: String
)
