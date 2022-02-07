package com.example.moviedb.di

import com.example.moviedb.repository.LocalMoviesRepository
import com.example.moviedb.repository.LocalMoviesRepositoryImpl
import com.example.moviedb.repository.MoviesRepository
import com.example.moviedb.repository.MoviesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiModule {
    @Binds
    abstract fun moviesRepositoryImpl(moviesRepositoryImpl: MoviesRepositoryImpl): MoviesRepository
    @Binds
    abstract fun localMoviesRepositoryImpl(localMoviesRepositoryImpl: LocalMoviesRepositoryImpl): LocalMoviesRepository
}