package com.mafrilearth.pokedex.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mafrilearth.pokedex.core.domain.usecase.PokemonUseCase
import com.mafrilearth.pokedex.model.PokemonUI
import com.mafrilearth.pokedex.utils.UIMapper.toUI
import com.mafrilearth.pokedex.utils.UIMapper.toDomain
import kotlinx.coroutines.flow.map

class FavoriteViewModel(private val pokemonUseCase: PokemonUseCase) : ViewModel() {
    val favoritePokemon = pokemonUseCase.getFavoritePokemon().map { list ->
        list.map { it.toUI() }
    }.asLiveData()
    
    fun setFavoritePokemon(pokemon: PokemonUI, state: Boolean) {
        pokemonUseCase.setFavoritePokemon(pokemon.toDomain(), state)
    }
}
