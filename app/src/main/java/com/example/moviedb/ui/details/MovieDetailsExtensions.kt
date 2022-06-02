package com.example.moviedb.ui.details

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.domain.models.Genre
import com.example.domain.models.MovieDetails
import com.example.domain.models.ProductionCompany
import com.example.moviedb.R
import com.example.moviedb.base.BaseActivity
import com.example.moviedb.databinding.ItemLocalMovieBinding
import com.example.moviedb.databinding.ItemTagsBinding
import com.example.moviedb.utils.Constants
import com.example.web.NetworkResult
import com.example.web.WebConstants
import com.google.android.material.chip.Chip

fun MovieDetailsFragment.showData(movieDetails: MovieDetails) {
    binding.ivMovie.load(WebConstants.BASE_IMG_URL + movieDetails.poster_path){
        crossfade(true)
        transformations(RoundedCornersTransformation(20f))
    }
    binding.tvTitleMovie.text = movieDetails.title
    binding.tvDescriptionMovie.text = if (movieDetails.overview.isNullOrEmpty()){
        getString(R.string.empty_description)
    } else {
        movieDetails.overview
    }
    binding.tvDetailsMovie.text = getString(R.string.release, movieDetails.release_date)
    binding.tvDurationMovie.text = getString(R.string.duration, movieDetails.runtime)
    binding.rbMovie.progress = movieDetails.vote_average!!.toInt()

    if (!movieDetails.production_companies.isNullOrEmpty()){
        (movieDetails.production_companies as MutableList<ProductionCompany>).forEach { company ->
            binding.chipsCompanies.addView(createTagChip(company.name))
        }
    }

    if (!movieDetails.genres.isNullOrEmpty()){
        (movieDetails.genres as MutableList<Genre>).forEach { genre ->
            binding.chipsGenres.addView(createTagChip(genre.name))
        }
    }

    val loader = ImageLoader(requireContext())
    val req = ImageRequest.Builder(requireContext())
        .data(WebConstants.BASE_IMG_URL + movieDetails.poster_path) // demo link
        .target { result ->
            if (activityViewModel.isReadyForNextChange){
                activityViewModel.backgroundForBlur.value =
                    (result as BitmapDrawable)
                        .bitmap
                        .copy(Bitmap.Config.ARGB_8888, true)
            }
        }
        .build()

    val disposable = loader.enqueue(req)

}

fun MovieDetailsFragment.initViewModel(){
    viewModel.getMovieDetails(args.movieId.toInt())
}

fun MovieDetailsFragment.initObserver(){
    viewModel.movieDetails.observe(viewLifecycleOwner){ result ->
        when(result){
            is NetworkResult.Success -> {
                binding.pbCircle.hide()
                result.data?.let {
                    showData(it)
                }
            }
            is NetworkResult.Error -> {
                binding.pbCircle.hide()
                Toast.makeText(requireContext(), getString(R.string.err_network), Toast.LENGTH_SHORT).show()
                if (args.origin == Constants.FRAGMENT_HOME) {
                    findNavController().navigate(MovieDetailsFragmentDirections.actionMovieDetailsFragmentToHomeFragment())
                } else {
                    findNavController().navigate(MovieDetailsFragmentDirections.actionMovieDetailsFragmentToFavoritesFragment())
                }
            }
            is NetworkResult.Loading -> {
                binding.pbCircle.show()
            }
        }
    }
}

private fun MovieDetailsFragment.createTagChip(chipName: String): Chip {
    return Chip(context).apply {
        setChipBackgroundColorResource(R.color.black_40)
        text = chipName
        isCloseIconVisible = false
        setTextColor(ContextCompat.getColor(context, R.color.white))
    }

}
