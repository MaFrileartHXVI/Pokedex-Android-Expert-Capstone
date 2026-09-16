package com.mafrilearth.pokedex.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonUI(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val height: Int = 0,
    val weight: Int = 0,
    val baseExperience: Int = 0,
    val types: List<String> = emptyList(),
    val stats: List<PokemonStatUI> = emptyList(),
    val abilities: List<String> = emptyList(),
    val forms: List<String> = emptyList(),
    val moves: List<String> = emptyList(),
    val species: String = "",
    val description: String = "",
    val evolutionChain: List<String> = emptyList(),
    val isFavorite: Boolean = false
) : Parcelable

@Parcelize
data class PokemonStatUI(
    val name: String,
    val baseStat: Int
) : Parcelable
