package com.example.web

import com.example.domain.models.Movie
import com.example.domain.models.MovieDetails
import com.example.domain.models.MoviePages
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    suspend fun getMoviesByPopularity(
        @Query("language") language: String,
        @Query("page") page: Number
    ): Response<MoviePages>

    @GET("movie/top_rated")
    suspend fun getMoviesByRating(
        @Query("language") language: String,
        @Query("page") page: Number
    ): Response<MoviePages>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Number,
        @Query("language") language: String,
    ): Response<MovieDetails>
}