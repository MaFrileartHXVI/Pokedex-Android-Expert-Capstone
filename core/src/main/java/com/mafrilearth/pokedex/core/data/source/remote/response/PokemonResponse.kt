package com.mafrilearth.pokedex.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("height")
    val height: Int,
    @field:SerializedName("weight")
    val weight: Int,
    @field:SerializedName("base_experience")
    val baseExperience: Int?,
    @field:SerializedName("types")
    val types: List<TypeSlotResponse>?,
    @field:SerializedName("stats")
    val stats: List<StatSlotResponse>?,
    @field:SerializedName("abilities")
    val abilities: List<AbilitySlotResponse>?,
    @field:SerializedName("forms")
    val forms: List<NameUrlResponse>?,
    @field:SerializedName("moves")
    val moves: List<MoveSlotResponse>?,
    @field:SerializedName("species")
    val species: NameUrlResponse?
)

data class TypeSlotResponse(
    @field:SerializedName("type")
    val type: NameUrlResponse
)

data class StatSlotResponse(
    @field:SerializedName("base_stat")
    val baseStat: Int,
    @field:SerializedName("stat")
    val stat: NameUrlResponse
)

data class AbilitySlotResponse(
    @field:SerializedName("ability")
    val ability: NameUrlResponse
)

data class MoveSlotResponse(
    @field:SerializedName("move")
    val move: NameUrlResponse
)

data class NameUrlResponse(
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("url")
    val url: String
)
