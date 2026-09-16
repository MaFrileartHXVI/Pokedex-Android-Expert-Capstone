package com.mafrilearth.pokedex.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mafrilearth.pokedex.core.domain.usecase.PokemonUseCase

import com.mafrilearth.pokedex.model.PokemonUI
import com.mafrilearth.pokedex.utils.UIMapper.toUI
import com.mafrilearth.pokedex.utils.UIMapper.toDomain
import kotlinx.coroutines.flow.map
import com.mafrilearth.pokedex.core.domain.Resource

class DetailViewModel(private val pokemonUseCase: PokemonUseCase) : ViewModel() {
    fun getPokemonDetail(id: Int) = pokemonUseCase.getPokemonDetail(id).map { resource ->
        when (resource) {
            is Resource.Success -> Resource.Success(resource.data?.toUI())
            is Resource.Error -> Resource.Error(resource.message ?: "Unknown Error", resource.data?.toUI())
            is Resource.Loading -> Resource.Loading(resource.data?.toUI())
        }
    }.asLiveData()
    
    fun setFavoritePokemon(pokemonUI: PokemonUI, newStatus: Boolean) =
        pokemonUseCase.setFavoritePokemon(pokemonUI.toDomain(), newStatus)
}
