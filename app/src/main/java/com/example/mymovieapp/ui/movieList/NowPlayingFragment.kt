package com.example.mymovieapp.ui.movieList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymovieapp.MainActivity
import com.example.mymovieapp.R
import com.example.mymovieapp.adapters.NowPlayingMovieListAdapter
import com.example.mymovieapp.adapters.PopularMovieListAdapter
import com.example.mymovieapp.data.model.responses.MovieData
import com.example.mymovieapp.databinding.FragmentNowPlayingBinding
import com.example.mymovieapp.ui.home.MovieViewModel
import com.example.mymovieapp.utils.Constants
import com.example.mymovieapp.utils.Resource


class NowPlayingFragment : Fragment(), NowPlayingMovieListAdapter.OnItemClickListener {

    lateinit var viewModel: MovieViewModel
    lateinit var movieAdapter: NowPlayingMovieListAdapter

    private var _binding: FragmentNowPlayingBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNowPlayingBinding.inflate(inflater, container, false)

        viewModel = (activity as MainActivity).movieViewModel
        setRecyclerView()

        viewModel.getNowPlayingMovies()

        viewModel.nowPlayingMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results.toList())
                        val totalPages = movieResponse.total_results / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.upcomingMoviePage == totalPages
                        if(isLastPage){
                            binding.rvNowPlayingMovie.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(requireContext(), "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        return binding.root
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getNowPlayingMovies()
                isScrolling = false
            }
        }
    }

    private fun hideProgressBar(){
        binding.progressBarLoading.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.progressBarLoading.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setRecyclerView(){
        movieAdapter = NowPlayingMovieListAdapter(this@NowPlayingFragment)
        binding.rvNowPlayingMovie.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addOnScrollListener(this@NowPlayingFragment.scrollListener)
        }
    }

    override fun onItemClick(movieData: MovieData) {
        val action = NowPlayingFragmentDirections.actionNowPlayingFragmentToDetailsFragment(movieData)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}