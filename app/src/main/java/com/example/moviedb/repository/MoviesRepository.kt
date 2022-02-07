package com.example.moviedb.repository

import com.example.domain.models.Movie
import com.example.domain.models.MovieDetails
import com.example.domain.models.MoviePages
import com.example.web.NetworkResult
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun getMoviesByPopularity(page: Number): Flow<NetworkResult<MoviePages>>
    suspend fun getMoviesByRating(page: Number): Flow<NetworkResult<MoviePages>>
    suspend fun getMovieDetails(movieId: Number): Flow<NetworkResult<MovieDetails>>
}