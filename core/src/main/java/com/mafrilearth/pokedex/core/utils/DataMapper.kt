package com.mafrilearth.pokedex.core.utils

import com.mafrilearth.pokedex.core.data.source.local.entity.PokemonEntity
import com.mafrilearth.pokedex.core.data.source.remote.response.PokemonResponse
import com.mafrilearth.pokedex.core.data.source.remote.response.PokemonResultResponse
import com.mafrilearth.pokedex.core.domain.model.Pokemon
import com.mafrilearth.pokedex.core.domain.model.PokemonStat

object DataMapper {
    fun mapResponsesToEntities(input: List<PokemonResultResponse>): List<PokemonEntity> {
        val pokemonList = ArrayList<PokemonEntity>()
        input.forEach {
            val idStr = it.url.split("/".toRegex()).dropLast(1).last()
            val id = idStr.toIntOrNull() ?: 0
            val pokemon = PokemonEntity(
                pokemonId = id,
                name = it.name,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
                isFavorite = false
            )
            pokemonList.add(pokemon)
        }
        return pokemonList
    }

    fun mapEntitiesToDomain(input: List<PokemonEntity>): List<Pokemon> =
        input.map {
            Pokemon(
                id = it.pokemonId,
                name = it.name,
                imageUrl = it.imageUrl,
                height = it.height,
                weight = it.weight,
                baseExperience = it.baseExperience,
                types = it.types,
                stats = it.stats,
                abilities = it.abilities,
                forms = it.forms,
                moves = it.moves,
                species = it.species,
                description = it.description,
                evolutionChain = it.evolutionChain,
                isFavorite = it.isFavorite
            )
        }

    fun mapDomainToEntity(input: Pokemon) = PokemonEntity(
        pokemonId = input.id,
        name = input.name,
        imageUrl = input.imageUrl,
        height = input.height,
        weight = input.weight,
        baseExperience = input.baseExperience,
        types = input.types,
        stats = input.stats,
        abilities = input.abilities,
        forms = input.forms,
        moves = input.moves,
        species = input.species,
        description = input.description,
        evolutionChain = input.evolutionChain,
        isFavorite = input.isFavorite
    )
    
    fun mapDetailResponseToEntity(input: PokemonResponse, isFavorite: Boolean): PokemonEntity =
        PokemonEntity(
            pokemonId = input.id,
            name = input.name,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${input.id}.png",
            height = input.height,
            weight = input.weight,
            baseExperience = input.baseExperience ?: 0,
            types = input.types?.map { it.type.name } ?: emptyList(),
            stats = input.stats?.map { PokemonStat(it.stat.name, it.baseStat) } ?: emptyList(),
            abilities = input.abilities?.map { it.ability.name } ?: emptyList(),
            forms = input.forms?.map { it.name } ?: emptyList(),
            moves = input.moves?.map { it.move.name } ?: emptyList(),
            species = input.species?.name ?: "",
            description = "", // Fetched separately
            evolutionChain = emptyList(), // Fetched separately
            isFavorite = isFavorite
        )
}
