package com.mafrilearth.pokedex.core.data.source.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mafrilearth.pokedex.core.domain.model.PokemonStat

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStatList(value: List<PokemonStat>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStatList(value: String?): List<PokemonStat>? {
        val listType = object : TypeToken<List<PokemonStat>>() {}.type
        return gson.fromJson(value, listType)
    }
}
