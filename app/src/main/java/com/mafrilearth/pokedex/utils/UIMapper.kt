package com.mafrilearth.pokedex.utils

import com.mafrilearth.pokedex.core.domain.model.Pokemon
import com.mafrilearth.pokedex.core.domain.model.PokemonStat
import com.mafrilearth.pokedex.model.PokemonStatUI
import com.mafrilearth.pokedex.model.PokemonUI

object UIMapper {
    fun Pokemon.toUI(): PokemonUI {
        return PokemonUI(
            id = this.id,
            name = this.name,
            imageUrl = this.imageUrl,
            height = this.height,
            weight = this.weight,
            baseExperience = this.baseExperience,
            types = this.types,
            stats = this.stats.map { it.toUI() },
            abilities = this.abilities,
            forms = this.forms,
            moves = this.moves,
            species = this.species,
            description = this.description,
            evolutionChain = this.evolutionChain,
            isFavorite = this.isFavorite
        )
    }

    fun PokemonStat.toUI(): PokemonStatUI {
        return PokemonStatUI(
            name = this.name,
            baseStat = this.baseStat
        )
    }

    fun PokemonUI.toDomain(): Pokemon {
        return Pokemon(
            id = this.id,
            name = this.name,
            imageUrl = this.imageUrl,
            height = this.height,
            weight = this.weight,
            baseExperience = this.baseExperience,
            types = this.types,
            stats = this.stats.map { it.toDomain() },
            abilities = this.abilities,
            forms = this.forms,
            moves = this.moves,
            species = this.species,
            description = this.description,
            evolutionChain = this.evolutionChain,
            isFavorite = this.isFavorite
        )
    }

    fun PokemonStatUI.toDomain(): PokemonStat {
        return PokemonStat(
            name = this.name,
            baseStat = this.baseStat
        )
    }
}
