package com.mafrilearth.pokedex.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mafrilearth.pokedex.core.domain.Resource
import com.mafrilearth.pokedex.core.domain.usecase.PokemonUseCase
import com.mafrilearth.pokedex.utils.UIMapper.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class HomeViewModel(pokemonUseCase: PokemonUseCase) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    
    @Suppress("unused")
    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    private val _typeFilter = MutableStateFlow("All")
    
    @Suppress("unused")
    fun setTypeFilter(type: String) {
        _typeFilter.value = type
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val pokemon = _typeFilter.flatMapLatest { type ->
        val sourceFlow = if (type == "All") {
            pokemonUseCase.getAllPokemon()
        } else {
            pokemonUseCase.getPokemonByType(type.lowercase())
        }
        
        combine(sourceFlow, searchQuery) { resource, query ->
            when (resource) {
                is Resource.Success -> {
                    val filteredList = if (query.isBlank()) {
                        resource.data
                    } else {
                        resource.data?.filter { it.name.contains(query, ignoreCase = true) }
                    }
                    val uiList = filteredList?.map { it.toUI() } ?: emptyList()
                    Resource.Success(uiList)
                }
                is Resource.Error -> {
                    val uiList = resource.data?.map { it.toUI() }
                    Resource.Error(resource.message ?: "Unknown Error", uiList)
                }
                is Resource.Loading -> {
                    val uiList = resource.data?.map { it.toUI() }
                    Resource.Loading(uiList)
                }
            }
        }
    }.asLiveData(viewModelScope.coroutineContext)
    
    val favoritePokemonCount = pokemonUseCase.getFavoritePokemon().map { it.size }.asLiveData(viewModelScope.coroutineContext)
}
