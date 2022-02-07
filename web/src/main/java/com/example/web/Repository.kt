package com.example.web


class Repository(private val apiService: ApiService) {
    suspend fun getMoviesByPopularity(page: Number) =
        apiService.getMoviesByPopularity(WebConstants.API_KEY, WebConstants.LANGUAGE, page)

    suspend fun getMoviesByRating(page: Number) =
        apiService.getMoviesByRating(WebConstants.API_KEY, WebConstants.LANGUAGE, page)

    suspend fun getMovieDetails(movieId: Number) =
        apiService.getMovieDetails(movieId, WebConstants.API_KEY, WebConstants.LANGUAGE)
}