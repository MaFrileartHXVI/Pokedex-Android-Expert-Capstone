package com.mafrilearth.pokedex.core.di

import androidx.room.Room
import com.mafrilearth.pokedex.core.data.PokemonRepository
import com.mafrilearth.pokedex.core.data.source.local.LocalDataSource
import com.mafrilearth.pokedex.core.data.source.local.room.PokemonDatabase
import com.mafrilearth.pokedex.core.data.source.remote.RemoteDataSource
import com.mafrilearth.pokedex.core.data.source.remote.network.ApiService
import com.mafrilearth.pokedex.core.domain.repository.IPokemonRepository
import com.mafrilearth.pokedex.core.utils.AppExecutors
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.CertificatePinner
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

val databaseModule = module {
    factory { get<PokemonDatabase>().pokemonDao() }
    single {
        System.loadLibrary("sqlcipher")
        val passPhrase = "dicoding".toByteArray()
        val factory = SupportOpenHelperFactory(passPhrase)
        Room.databaseBuilder(
            androidContext(),
            PokemonDatabase::class.java, "Pokemon.db"
        ).fallbackToDestructiveMigration()
        .openHelperFactory(factory)
        .build()
    }
}

val networkModule = module {
    single {
        val hostname = "pokeapi.co"
        val certificatePinner = CertificatePinner.Builder()
            .add(hostname, "sha256/vI2c4MzHEbIyjzPN4chWo00EfZeCrlu7OrQuswZxK5Q=")
            .add(hostname, "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=")
            .add(hostname, "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c=")
            .build()
        OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    factory { AppExecutors() }
    single<IPokemonRepository> {
        PokemonRepository(
            get(),
            get(),
            get()
        )
    }
}
