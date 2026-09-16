package com.mafrilearth.pokedex.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextResponse>?,
    @field:SerializedName("color")
    val color: NameUrlResponse?,
    @field:SerializedName("habitat")
    val habitat: NameUrlResponse?,
    @field:SerializedName("generation")
    val generation: NameUrlResponse?,
    @field:SerializedName("evolution_chain")
    val evolutionChain: EvolutionChainUrlResponse?,
    @field:SerializedName("evolves_from_species")
    val evolvesFromSpecies: NameUrlResponse?
)

data class FlavorTextResponse(
    @field:SerializedName("flavor_text")
    val flavorText: String,
    @field:SerializedName("language")
    val language: NameUrlResponse
)

data class EvolutionChainUrlResponse(
    @field:SerializedName("url")
    val url: String
)
