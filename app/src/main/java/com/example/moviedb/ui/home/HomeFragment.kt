package com.example.moviedb.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.domain.models.Movie
import com.example.moviedb.base.BaseViewModel
import com.example.moviedb.databinding.FragmentHomeBinding
import com.example.moviedb.utils.BaseAdapter
import com.example.moviedb.utils.EndlessRecyclerViewScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val viewModel: HomeVM by viewModels()
    val activityViewModel: BaseViewModel by activityViewModels()
    val binding get() = _binding!!
    var mAdapter = BaseAdapter<Movie>()
    var position = 0
    var scrollListener: EndlessRecyclerViewScrollListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initView()
        initRecyclerView()
        initObservers()
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