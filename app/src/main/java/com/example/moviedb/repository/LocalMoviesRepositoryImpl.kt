package com.example.moviedb.repository

import com.example.database.DBMovie
import com.example.domain.models.Movie
import com.example.domain.models.MoviePages
import com.example.web.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalMoviesRepositoryImpl @Inject constructor(
    private val db: DBMovie
) : LocalMoviesRepository {
    override suspend fun getMovies() = db.movieDao().getMovies()

    override suspend fun saveMovie(movie: Movie) = flow {
        emit(db.movieDao().insert(movie))
    }

    override suspend fun deleteMovie(movie: Movie) = flow {
        emit(db.movieDao().deleteMovie(movie))
    }
}