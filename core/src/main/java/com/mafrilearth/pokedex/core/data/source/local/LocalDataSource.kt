package com.mafrilearth.pokedex.core.data.source.local

import com.mafrilearth.pokedex.core.data.source.local.entity.PokemonEntity
import com.mafrilearth.pokedex.core.data.source.local.room.PokemonDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val pokemonDao: PokemonDao) {
    fun getAllPokemon(): Flow<List<PokemonEntity>> = pokemonDao.getAllPokemon()
    
    fun getPokemonByType(type: String): Flow<List<PokemonEntity>> = pokemonDao.getPokemonByType(type)
    fun getFavoritePokemon(): Flow<List<PokemonEntity>> = pokemonDao.getFavoritePokemon()
    suspend fun insertPokemon(pokemonList: List<PokemonEntity>) = pokemonDao.insertPokemon(pokemonList)
    fun setFavoritePokemon(pokemon: PokemonEntity, newState: Boolean) {
        pokemon.isFavorite = newState
        pokemonDao.updateFavoritePokemon(pokemon)
    }
    fun getPokemonDetail(id: Int): Flow<PokemonEntity?> = pokemonDao.getPokemonDetail(id)
    suspend fun getPokemonDetailSync(id: Int): PokemonEntity? = pokemonDao.getPokemonDetailSync(id)
}
