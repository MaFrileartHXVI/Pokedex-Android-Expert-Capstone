package com.mafrilearth.pokedex.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mafrilearth.pokedex.core.data.source.local.entity.PokemonEntity

@Database(entities = [PokemonEntity::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}
