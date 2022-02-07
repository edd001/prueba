package com.example.moviedb.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.domain.models.Genre
import com.example.domain.models.Movie
import com.example.domain.models.ProductionCompany
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentMovieDetailsBinding
import com.example.moviedb.ui.favorites.FavoritesVM
import com.example.moviedb.utils.BaseAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private var _binding: FragmentMovieDetailsBinding? = null
    val viewModel: MovieDetailsVM by viewModels()
    var mAdapterGenres = BaseAdapter<Genre>()
    var mAdapterCompanies = BaseAdapter<ProductionCompany>()
    val args: MovieDetailsFragmentArgs by navArgs()
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
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