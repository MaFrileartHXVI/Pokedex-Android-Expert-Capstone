package com.mafrilearth.pokedex.favorite.di

import com.mafrilearth.pokedex.favorite.FavoriteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoriteModule = module {
    viewModel { FavoriteViewModel(get()) }
}
