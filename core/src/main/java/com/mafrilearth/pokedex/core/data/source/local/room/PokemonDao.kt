package com.mafrilearth.pokedex.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mafrilearth.pokedex.core.data.source.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon WHERE isBaseEvolution = 1")
    fun getAllPokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE types LIKE '%' || :type || '%'")
    fun getPokemonByType(type: String): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE isFavorite = 1")
    fun getFavoritePokemon(): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: List<PokemonEntity>)

    @Update
    fun updateFavoritePokemon(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon WHERE pokemonId = :id")
    fun getPokemonDetail(id: Int): Flow<PokemonEntity?>

    @Query("SELECT * FROM pokemon WHERE pokemonId = :id")
    suspend fun getPokemonDetailSync(id: Int): PokemonEntity?
}
