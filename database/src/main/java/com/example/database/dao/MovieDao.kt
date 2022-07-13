package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import com.example.domain.models.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie): Long

    @Query("SELECT * FROM movie")
    fun getMovies(): Flow<List<Movie>>

    @Delete
    suspend fun deleteMovie(movie: Movie): Int

    @Query("DELETE FROM movie")
    suspend fun deleteAll()

    @Update
    suspend fun update(movie: Movie)
}