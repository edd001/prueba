package com.example.moviedb.ui.home

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
import com.example.moviedb.base.BaseActivity
import com.example.moviedb.databinding.ItemMovieBinding
import com.example.moviedb.utils.Constants
import com.example.moviedb.utils.EndlessRecyclerViewScrollListener
import com.example.web.NetworkResult
import com.example.web.WebConstants

fun HomeFragment.initViewModel(){
    viewModel.fetchMoviesByPopularity()
}

fun HomeFragment.initView(){
    val items = resources.getStringArray(R.array.order_array)

    binding.lblEmpty.setOnClickListener {
        viewModel.fetchMoviesByPopularity()
    }

    val spinnerAdapter = object : ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item, items) {
        override fun isEnabled(position: Int): Boolean {
            // Disable the first item from Spinner
            // First item will be used for hint
            return position != 0
        }

        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
            //set the color of first item in the drop down list to gray
            if(position == 0) {
                view.setTextColor(Color.GRAY)
            } else {
                view.setTextColor(Color.BLACK)
            }
            return view
        }
    }

    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    binding.spinner.adapter = spinnerAdapter

    binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            view?.let {
                when (parent!!.getItemAtPosition(position).toString()) {
                    items[0] -> {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                    items[1] -> {
                        (view as TextView).setTextColor(Color.WHITE)
                        viewModel.fetchMoviesByPopularity()
                        viewModel.areOptionsChanged = true
                        viewModel.isRatingMovies = false
                        viewModel.pageCounter = Constants.FIRST_PAGE
                    }
                    items[2] -> {
                        (view as TextView).setTextColor(Color.WHITE)
                        viewModel.fetchMoviesByRating()
                        viewModel.areOptionsChanged = true
                        viewModel.isRatingMovies = true
                        viewModel.pageCounter = Constants.FIRST_PAGE
                    }
                }
            }
        }
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
                val loader = ImageLoader(requireContext())
                val req = ImageRequest.Builder(requireContext())
                    .data(WebConstants.BASE_IMG_URL + viewModel.listMovies[id].poster_path) // demo link
                    .target { result ->
                        (activity as BaseActivity?)?.changeBackground((result as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true))
                    }
                    .build()

                val disposable = loader.enqueue(req)
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

    mAdapter.expressionViewHolderBinding = { item, viewBinding, position, adapter->
        val view = viewBinding as ItemMovieBinding

        view.ivMovie.load(WebConstants.BASE_IMG_URL + item.poster_path){
            crossfade(true)
            transformations(RoundedCornersTransformation(20f))
        }

        view.ivMovie.clipToOutline = true
        view.tvDescriptionMovie.text = if (item.overview.isNullOrEmpty()){
            getString(R.string.empty_description)
        } else {
            item.overview
        }
        view.tvDetailsMovie.text = resources.getString(R.string.date_of_release, item.release_date)
        view.tvTitleMovie.text = item.title
        view.rbMovie.max = 10
        view.rbMovie.rating = item.vote_average!!.toFloat()

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
                itemGone(binding.pbCircle)
                itemVisible(binding.rvMovies)
                result.data?.let {
                    viewModel.pageCounter = it.page
                    viewModel.pageCounterMax = it.total_pages
                    if (viewModel.areOptionsChanged){
                        viewModel.listMovies = it.results
                        viewModel.areOptionsChanged = false
                    } else {
                        viewModel.listMovies = (viewModel.listMovies + it.results) as ArrayList<Movie>
                    }
                    mAdapter.listOfItems = viewModel.listMovies
                }
            }
            is NetworkResult.Error -> {
                itemGone(binding.pbCircle)
                itemGone(binding.rvMovies)
                itemVisible(binding.lblEmpty)
            }
            is NetworkResult.Loading -> {
                itemVisible(binding.pbCircle)
                itemGone(binding.rvMovies)
                itemGone(binding.lblEmpty)
            }
        }
    }
}

fun HomeFragment.itemGone(item: View){
    item.visibility = View.GONE
}

fun HomeFragment.itemVisible(item: View){
    item.visibility = View.VISIBLE
}