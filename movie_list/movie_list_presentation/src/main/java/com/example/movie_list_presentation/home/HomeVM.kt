package com.example.movie_list_presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_list_domain.model.Movie
import com.example.moviedb.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
//    private val moviesRepository: MoviesRepository,
//    private val localMoviesRepository: LocalMoviesRepository
): ViewModel()  {
//    private val _movies = MutableLiveData<NetworkResult<MoviePages>>()
    private val _saveRes = MutableLiveData<Long>()
    private val saveRes: LiveData<Long> = _saveRes
//    val movies: LiveData<NetworkResult<MoviePages>> = _movies
    var pageCounter = Constants.FIRST_PAGE
    var pageCounterMax = Constants.FIRST_PAGE
//    var listMovies = arrayListOf<Movie>()
    var areOptionsChanged = false
    var isRatingMovies = false

    fun fetchMoviesByPopularity(page: Int? = Constants.FIRST_PAGE) = viewModelScope.launch {
//        moviesRepository.getMoviesByPopularity(page!!).collect {
//            _movies.value = it
//        }
    }

    fun fetchMoviesByRating(page: Int? = Constants.FIRST_PAGE) = viewModelScope.launch {
//        moviesRepository.getMoviesByRating(page!!).collect {
//            _movies.value = it
//        }
    }

    fun saveMovie(movie: Movie) = viewModelScope.launch {
//        localMoviesRepository.saveMovie(movie).collect {
//            _saveRes.value = it
//        }
    }
}