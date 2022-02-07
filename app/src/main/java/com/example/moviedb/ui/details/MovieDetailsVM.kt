package com.example.moviedb.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.MovieDetails
import com.example.moviedb.repository.MoviesRepository
import com.example.web.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsVM @Inject constructor(
    private val moviesRepository: MoviesRepository
): ViewModel() {
    private val _movieDetails = MutableLiveData<NetworkResult<MovieDetails>>()
    val movieDetails: LiveData<NetworkResult<MovieDetails>> = _movieDetails

    fun getMovieDetails(movieId: Number) = viewModelScope.launch {
        moviesRepository.getMovieDetails(movieId).collect {
            _movieDetails.value = it
        }
    }
}