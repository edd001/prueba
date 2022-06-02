package com.example.moviedb.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.example.domain.models.Movie
import com.example.moviedb.R
import com.example.moviedb.databinding.ItemMovieBinding
import com.example.moviedb.utils.Constants
import com.example.moviedb.utils.EndlessRecyclerViewScrollListener
import com.example.moviedb.utils.show
import com.example.moviedb.utils.hide
import com.example.web.NetworkResult
import com.example.web.WebConstants

fun HomeFragment.initViewModel(){
    viewModel.fetchMoviesByPopularity()
}

fun HomeFragment.initView(){
    binding.lblEmpty.setOnClickListener {
        viewModel.fetchMoviesByPopularity()
    }
}

fun HomeFragment.initRecyclerView(){
    val linearLayout = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

    scrollListener = object : EndlessRecyclerViewScrollListener(linearLayout) {
        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(view, dx, dy)
            val tempPosition = (binding.rvMovies.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (position != tempPosition) {
                val id = if (tempPosition > position){
                    tempPosition + 1
                }else{
                    tempPosition
                }
                changeBackground(id)
                position = tempPosition
            }
        }
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            // Triggered only when new data needs to be appended to the list
            // Add whatever code is needed to append new items to the bottom of the list
            viewModel.pageCounter++
            if (viewModel.isRatingMovies){
                viewModel.fetchMoviesByRating(viewModel.pageCounter)
            } else {
                viewModel.fetchMoviesByPopularity(viewModel.pageCounter)
            }
        }
    }

    mAdapter.expressionOnCreateViewHolder = { viewGroup->
        //Inflate the layout and send it to the adapter
        ItemMovieBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
    }

    mAdapter.expressionViewHolderBinding = { item, viewBinding, position, adapter ->
        val view = viewBinding as ItemMovieBinding

        view.ivMovie.load(WebConstants.BASE_IMG_URL + item.poster_path){
            crossfade(true)
            transformations(RoundedCornersTransformation(20f))
        }

        view.ivMovie.clipToOutline = true
        view.tvDetailsMovie.text = resources.getString(R.string.date_of_release, item.release_date)
        view.tvTitleMovie.text = item.title
        view.rbMovie.progress = item.vote_average?.toInt() ?: 0

        view.ivLike.setOnClickListener {
            viewModel.saveMovie(item)
            view.ivLike.load(getDrawable(requireContext(), R.drawable.ic_baseline_favorite_24))
            adapter.notifyItemChanged(position + 1)
        }

        view.root.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(item.id.toString(), Constants.FRAGMENT_HOME)
            findNavController().navigate(action)
        }
    }

    binding.rvMovies.addOnScrollListener(scrollListener!!)
    binding.rvMovies.layoutManager = linearLayout
    binding.rvMovies.adapter = mAdapter

}

fun HomeFragment.initObservers(){
    viewModel.movies.observe(viewLifecycleOwner){ result ->
        when(result){
            is NetworkResult.Success -> {
                binding.pbCircle.hide()
                binding.rvMovies.show()
                result.data?.let {
                    viewModel.pageCounter = it.page
                    viewModel.pageCounterMax = it.total_pages
                    if (viewModel.areOptionsChanged){
                        viewModel.listMovies = it.results
                        viewModel.areOptionsChanged = false
                    } else {
                        viewModel.listMovies = (viewModel.listMovies + it.results) as ArrayList<Movie>
                    }
                    if (activityViewModel.isFirstBackgroundForBlur){
                        changeBackground(Constants.FIRST_ELEMENT)
                        activityViewModel.isFirstBackgroundForBlur = false
                    }

                    mAdapter.listOfItems = viewModel.listMovies
                }
            }
            is NetworkResult.Error -> {
                binding.pbCircle.hide()
                binding.rvMovies.hide()
                binding.lblEmpty.show()
            }
            is NetworkResult.Loading -> {
                binding.pbCircle.show()
                binding.rvMovies.hide()
                binding.lblEmpty.hide()
            }
        }
    }
    activityViewModel.spinnerIndex.observe(viewLifecycleOwner){
        when (it) {
            activityViewModel.items[1] -> {
                viewModel.fetchMoviesByPopularity()
                viewModel.areOptionsChanged = true
                viewModel.isRatingMovies = false
                viewModel.pageCounter = Constants.FIRST_PAGE
            }
            activityViewModel.items[2] -> {
                viewModel.fetchMoviesByRating()
                viewModel.areOptionsChanged = true
                viewModel.isRatingMovies = true
                viewModel.pageCounter = Constants.FIRST_PAGE
            }
        }
    }
}

fun HomeFragment.changeBackground(id: Int){
    val loader = ImageLoader(requireContext())
    val req = ImageRequest.Builder(requireContext())
        .data(WebConstants.BASE_IMG_URL + viewModel.listMovies[id].poster_path) // demo link
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