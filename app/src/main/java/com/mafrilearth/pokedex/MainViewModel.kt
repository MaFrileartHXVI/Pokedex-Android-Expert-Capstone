package com.mafrilearth.pokedex

import androidx.lifecycle.ViewModel

import com.mafrilearth.pokedex.core.domain.usecase.PokemonUseCase

class MainViewModel(pokemonUseCase: PokemonUseCase) : ViewModel()
