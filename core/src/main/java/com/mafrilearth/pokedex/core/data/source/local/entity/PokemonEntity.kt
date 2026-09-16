package com.mafrilearth.pokedex.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mafrilearth.pokedex.core.domain.model.PokemonStat

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey
    @ColumnInfo(name = "pokemonId")
    var pokemonId: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "imageUrl")
    var imageUrl: String,

    @ColumnInfo(name = "height")
    var height: Int = 0,

    @ColumnInfo(name = "weight")
    var weight: Int = 0,

    @ColumnInfo(name = "baseExperience")
    var baseExperience: Int = 0,

    @ColumnInfo(name = "types")
    var types: List<String> = emptyList(),

    @ColumnInfo(name = "stats")
    var stats: List<PokemonStat> = emptyList(),

    @ColumnInfo(name = "abilities")
    var abilities: List<String> = emptyList(),

    @ColumnInfo(name = "forms")
    var forms: List<String> = emptyList(),

    @ColumnInfo(name = "moves")
    var moves: List<String> = emptyList(),

    @ColumnInfo(name = "species")
    var species: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "evolutionChain")
    var evolutionChain: List<String> = emptyList(),

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false,

    @ColumnInfo(name = "isBaseEvolution")
    var isBaseEvolution: Boolean = false
)
