package com.mafrilearth.pokedex.di

import com.mafrilearth.pokedex.MainViewModel
import com.mafrilearth.pokedex.core.domain.usecase.PokemonInteractor
import com.mafrilearth.pokedex.core.domain.usecase.PokemonUseCase
import com.mafrilearth.pokedex.ui.detail.DetailViewModel
import com.mafrilearth.pokedex.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<PokemonUseCase> { PokemonInteractor(get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}
