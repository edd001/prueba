package com.example.moviedb.repository

import com.example.web.*
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MoviesRepository, BaseApiResponse() {
    override suspend fun getMoviesByPopularity(page: Number) = safeApiCall {
        apiService.getMoviesByPopularity(WebConstants.API_KEY, WebConstants.LANGUAGE, page)
    }

    override suspend fun getMoviesByRating(page: Number) = safeApiCall {
        apiService.getMoviesByRating(WebConstants.API_KEY, WebConstants.LANGUAGE, page)
    }


    override suspend fun getMovieDetails(movieId: Number) = safeApiCall {
        apiService.getMovieDetails(movieId, WebConstants.API_KEY, WebConstants.LANGUAGE)
    }

}