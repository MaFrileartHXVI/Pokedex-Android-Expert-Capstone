package com.mafrilearth.pokedex.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class TypeDetailResponse(
    @field:SerializedName("pokemon")
    val pokemon: List<TypePokemonResponse>
)

data class TypePokemonResponse(
    @field:SerializedName("pokemon")
    val pokemon: PokemonResultResponse
)
