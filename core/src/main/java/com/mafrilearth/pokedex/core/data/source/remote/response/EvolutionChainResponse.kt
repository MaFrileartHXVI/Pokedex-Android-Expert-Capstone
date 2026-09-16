package com.mafrilearth.pokedex.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class EvolutionChainResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("chain")
    val chain: ChainLinkResponse
)

data class ChainLinkResponse(
    @field:SerializedName("species")
    val species: NameUrlResponse,
    @field:SerializedName("evolves_to")
    val evolvesTo: List<ChainLinkResponse>
)
