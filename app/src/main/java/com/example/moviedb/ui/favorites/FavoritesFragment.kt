package com.example.moviedb.ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.domain.models.Movie
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentFavoritesBinding
import com.example.moviedb.databinding.FragmentHomeBinding
import com.example.moviedb.utils.BaseAdapter
import com.example.moviedb.utils.EndlessRecyclerViewScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    val viewModel: FavoritesVM by viewModels()
    var mAdapter = BaseAdapter<Movie>()
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        initView()
        initRecyclerView()
        initObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}