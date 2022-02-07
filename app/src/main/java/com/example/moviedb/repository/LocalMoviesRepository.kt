package com.example.moviedb.repository

import com.example.domain.models.Movie
import com.example.domain.models.MoviePages
import com.example.web.NetworkResult
import kotlinx.coroutines.flow.Flow

interface LocalMoviesRepository {
    suspend fun getMovies(): Flow<List<Movie>>
    suspend fun saveMovie(movie: Movie): Flow<Long>
    suspend fun deleteMovie(movie: Movie): Flow<Int>
}