package com.example.moviedb.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Movie
import com.example.moviedb.repository.LocalMoviesRepository
import com.example.moviedb.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesVM @Inject constructor(
    private val localMoviesRepository: LocalMoviesRepository
): ViewModel() {
    private val _favoriteList = MutableLiveData<List<Movie>>()
    private val _deleteRes = MutableLiveData<Int>()
    val deleteRes: LiveData<Int> = _deleteRes
    val favoriteList: LiveData<List<Movie>> = _favoriteList

    fun getLocalMovies() = viewModelScope.launch {
        localMoviesRepository.getMovies().collect {
            _favoriteList.value = it
        }
    }

    fun deleteLocalMovie(movie: Movie) = viewModelScope.launch {
        localMoviesRepository.deleteMovie(movie).collect {
            _deleteRes.value = it
        }
    }

}