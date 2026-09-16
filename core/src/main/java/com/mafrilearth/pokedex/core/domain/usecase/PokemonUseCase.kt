package com.mafrilearth.pokedex.core.domain.usecase

import com.mafrilearth.pokedex.core.domain.model.Pokemon
import com.mafrilearth.pokedex.core.domain.Resource
import kotlinx.coroutines.flow.Flow

interface PokemonUseCase {
    fun getAllPokemon(): Flow<Resource<List<Pokemon>>>
    fun getPokemonByType(type: String): Flow<Resource<List<Pokemon>>>
    fun getFavoritePokemon(): Flow<List<Pokemon>>
    fun setFavoritePokemon(pokemon: Pokemon, state: Boolean)
    fun getPokemonDetail(id: Int): Flow<Resource<Pokemon>>
}
