package com.example.mymovieapp.ui.search

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymovieapp.MainActivity
import com.example.mymovieapp.R
import com.example.mymovieapp.adapters.SearchMovieAdapter
import com.example.mymovieapp.data.model.responses.MovieData
import com.example.mymovieapp.databinding.FragmentSearchBinding
import com.example.mymovieapp.ui.home.MovieViewModel
import com.example.mymovieapp.utils.Constants
import com.example.mymovieapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.mymovieapp.utils.Resource


class SearchFragment : Fragment(), SearchMovieAdapter.OnItemClickListenerSM {

    lateinit var viewModel: MovieViewModel
    lateinit var searchAdapter: SearchMovieAdapter

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var searchKey: String? = null

    private val args by navArgs<SearchFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).movieViewModel
        setRecyclerView()

        binding.apply {
            searchKey = args.searchKey
            viewModel.getSearchMovies(searchKey!!)
        }

        viewModel.searchMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        searchAdapter.differ.submitList(movieResponse.results.toList())
                        val totalPages = movieResponse.total_results / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.upcomingMoviePage == totalPages
                        if(isLastPage){
                            binding.rvSearchMovie.setPadding(0, 0, 0, 0)
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

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun hideProgressBar(){
        binding.progressBarLoading.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.progressBarLoading.visibility = View.VISIBLE
        isLoading = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_movie, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    binding.rvSearchMovie.scrollToPosition(0)
                    searchKey = query
                    viewModel.searchMoviePage = 1
                    viewModel.searchMovieResponse = null
                    viewModel.getSearchMovies(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
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
                viewModel.getSearchMovies(searchKey!!)
                isScrolling = false
            }
        }
    }


    private fun setRecyclerView(){
        searchAdapter = SearchMovieAdapter(this@SearchFragment)
        binding.rvSearchMovie.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    override fun onItemClickSM(movieData: MovieData) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(movieData)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}