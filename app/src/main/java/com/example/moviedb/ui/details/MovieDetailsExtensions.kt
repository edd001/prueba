package com.example.moviedb.ui.details

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
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
    mAdapterCompanies.listOfItems = movieDetails.production_companies as MutableList<ProductionCompany>?
    mAdapterGenres.listOfItems = movieDetails.genres as MutableList<Genre>?
    binding.rbMovie.progress = movieDetails.vote_average!!.toInt()


    val loader = ImageLoader(requireContext())
    val req = ImageRequest.Builder(requireContext())
        .data(WebConstants.BASE_IMG_URL + movieDetails.poster_path) // demo link
        .target { result ->
            (activity as BaseActivity?)?.changeBackground((result as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true))
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

fun MovieDetailsFragment.initRecyclerView(){
    val linearLayoutGenres = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    val linearLayoutCompany = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    mAdapterCompanies.expressionOnCreateViewHolder = { viewGroup ->
        //Inflate the layout and send it to the adapter
        ItemTagsBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
    }

    mAdapterCompanies.expressionViewHolderBinding = { item, viewBinding, position, adapter ->
        val view = viewBinding as ItemTagsBinding
        view.tvTitleMovie.text = item.name

    }

    mAdapterGenres.expressionOnCreateViewHolder = { viewGroup ->
        //Inflate the layout and send it to the adapter
        ItemTagsBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
    }

    mAdapterGenres.expressionViewHolderBinding = { item, viewBinding, position, adapter ->
        val view = viewBinding as ItemTagsBinding
        view.tvTitleMovie.text = item.name

    }

    binding.rvGenres.layoutManager = linearLayoutGenres
    binding.rvGenres.adapter = mAdapterGenres

    binding.rvCompanies.layoutManager = linearLayoutCompany
    binding.rvCompanies.adapter = mAdapterCompanies

}