package com.mafrilearth.pokedex.core.domain.usecase

import com.mafrilearth.pokedex.core.domain.model.Pokemon
import com.mafrilearth.pokedex.core.domain.repository.IPokemonRepository

class PokemonInteractor(private val pokemonRepository: IPokemonRepository) : PokemonUseCase {
    override fun getAllPokemon() = pokemonRepository.getAllPokemon()
    
    override fun getPokemonByType(type: String) = pokemonRepository.getPokemonByType(type)

    override fun getFavoritePokemon() = pokemonRepository.getFavoritePokemon()

    override fun setFavoritePokemon(pokemon: Pokemon, state: Boolean) = pokemonRepository.setFavoritePokemon(pokemon, state)

    override fun getPokemonDetail(id: Int) = pokemonRepository.getPokemonDetail(id)
}
