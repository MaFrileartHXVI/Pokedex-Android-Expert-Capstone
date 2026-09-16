package com.mafrilearth.pokedex

import android.app.Application
import com.mafrilearth.pokedex.core.di.databaseModule
import com.mafrilearth.pokedex.core.di.networkModule
import com.mafrilearth.pokedex.core.di.repositoryModule
import com.mafrilearth.pokedex.di.useCaseModule
import com.mafrilearth.pokedex.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}
