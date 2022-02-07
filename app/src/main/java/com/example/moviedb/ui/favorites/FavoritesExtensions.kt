package com.example.moviedb.ui.favorites

import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.domain.models.Movie
import com.example.moviedb.databinding.ItemLocalMovieBinding
import com.example.moviedb.utils.Constants
import com.example.web.WebConstants

fun FavoritesFragment.initView(){

}

fun FavoritesFragment.initViewModel(){
    viewModel.getLocalMovies()
}

fun FavoritesFragment.initObserver(){
    viewModel.favoriteList.observe(viewLifecycleOwner){
        if (it.isEmpty()){
            binding.lblEmpty.visibility = View.VISIBLE
            binding.rvMovies.visibility = View.GONE
        } else {
            binding.lblEmpty.visibility = View.GONE
            mAdapter.listOfItems = it as MutableList<Movie>?
        }
    }
    viewModel.deleteRes.observe(viewLifecycleOwner){

    }
}

fun FavoritesFragment.initRecyclerView(){
    val gridLayout = GridLayoutManager(requireContext(),2)

    mAdapter.expressionOnCreateViewHolder = { viewGroup->
        //Inflate the layout and send it to the adapter
        ItemLocalMovieBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
    }

    mAdapter.expressionViewHolderBinding = { item, viewBinding, position, adapter->
        val view = viewBinding as ItemLocalMovieBinding

        view.ivMovie.load(WebConstants.BASE_IMG_URL + item.poster_path){
            crossfade(true)
            transformations(RoundedCornersTransformation(20f))
        }
        view.ivMovie.clipToOutline = true
        view.tvTitleMovie.text = item.title

        view.ivDelete.setOnClickListener {
            viewModel.deleteLocalMovie(item)
            adapter.notifyItemRemoved(position + 1)
        }

        view.root.setOnClickListener {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToMovieDetailsFragment(item.id.toString(), Constants.FRAGMENT_FAVORITE)
            findNavController().navigate(action)
        }

        if(adapter.itemCount == 0){
            binding.lblEmpty.visibility = View.VISIBLE
            viewModel.getLocalMovies()
        }
    }

    binding.rvMovies.layoutManager = gridLayout
    binding.rvMovies.adapter = mAdapter

}