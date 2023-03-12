package com.example.mymovieapp.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.mymovieapp.MainActivity
import com.example.mymovieapp.R
import com.example.mymovieapp.adapters.NowPlayingMovieAdapter
import com.example.mymovieapp.adapters.PopularMovieAdapter
import com.example.mymovieapp.adapters.TrendingMovieAdapter
import com.example.mymovieapp.adapters.UpcomingMovieAdapter
import com.example.mymovieapp.data.model.responses.MovieData
import com.example.mymovieapp.databinding.FragmentHomeBinding
import com.example.mymovieapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.mymovieapp.utils.Resource
import kotlin.math.abs

class HomeFragment : Fragment(), NowPlayingMovieAdapter.OnItemClickListenerNP,
    PopularMovieAdapter.OnItemClickListenerPM, UpcomingMovieAdapter.OnItemClickListenerUC {

    lateinit var viewModel: MovieViewModel
    lateinit var movieAdapter: PopularMovieAdapter
    lateinit var nowPlayingMovieAdapter: NowPlayingMovieAdapter
    lateinit var upcomingMovieAdapter: UpcomingMovieAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var adapterTwo: TrendingMovieAdapter

    private var searchKey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel = (activity as MainActivity).movieViewModel
        setRecyclerView()

        init()
        setUpTransformer()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
            }
        })

        viewModel.trendingMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->

                        adapterTwo = TrendingMovieAdapter(movieResponse.results as ArrayList<MovieData>, viewPager2)
                        viewPager2.adapter = adapterTwo
                        viewPager2.offscreenPageLimit = 3
                        viewPager2.clipToPadding = false
                        viewPager2.clipChildren = false
                        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

                        val totalPages = movieResponse.total_results / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.trendingMoviePage == totalPages
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

        viewModel.popularMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results.toList())
                        val totalPages = movieResponse.total_results / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.popularMoviePage == totalPages
                        if(isLastPage){
                            binding.rvPopularMovies.setPadding(0, 0, 0, 0)
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

        viewModel.nowPlayingMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        nowPlayingMovieAdapter.differ.submitList(movieResponse.results.toList())
                        val totalPages = movieResponse.total_results / QUERY_PAGE_SIZE + 2
                        isLastPageNP = viewModel.nowPlayingMoviePage == totalPages
                        if(isLastPageNP){
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

        viewModel.upcomingMovies.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        Log.e("=====", ""+ movieResponse.results)
                        upcomingMovieAdapter.differ.submitList(movieResponse.results.toList())
                        val totalPages = movieResponse.total_results / QUERY_PAGE_SIZE + 2
                        isLastPageUp = viewModel.upcomingMoviePage == totalPages
                        if(isLastPageUp){
                            binding.rvUpcomingMovie.setPadding(0, 0, 0, 0)
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

        binding.btnSeeAllTrending.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToTrendingFragment()
            findNavController().navigate(action)
        }

        binding.btnSeeListPopular.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPopularFragment()
            findNavController().navigate(action)
        }

        binding.btnSeeAllNowPlaying.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNowPlayingFragment()
            findNavController().navigate(action)
        }

        binding.btnSeeAllUpcoming.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToUpcomingFragment()
            findNavController().navigate(action)
        }

        setHasOptionsMenu(true)

        return root
    }

    private fun hideProgressBar(){
        binding.progressBarLoading.visibility = View.INVISIBLE
        isLoading = false
        isLoadingNP = false
        isLoadingUp = false
    }

    private fun showProgressBar(){
        binding.progressBarLoading.visibility = View.VISIBLE
        isLoading = true
        isLoadingNP = true
        isLoadingUp = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_movie, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    searchKey = query
                    val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(searchKey as String)
                    findNavController().navigate(action)
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getPopularMovies()
                isScrolling = false
            }
        }
    }

    var isLoadingNP = false
    var isLastPageNP = false
    var isScrollingNP = false

    val scrollListnerNowPlaying = object : RecyclerView.OnScrollListener(){

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrollingNP = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoadingNP && !isLastPageNP
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrollingNP
            if(shouldPaginate){
                viewModel.getNowPlayingMovies()
                isScrollingNP = false
            }
        }
    }

    var isLoadingUp = false
    var isLastPageUp = false
    var isScrollingUp = false

    val scrollListnerUpcoming = object : RecyclerView.OnScrollListener(){

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrollingUp = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoadingUp && !isLastPageUp
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrollingUp
            if(shouldPaginate){
                viewModel.getUpcomingMovies()
                isScrollingUp = false
            }
        }
    }

    private fun setRecyclerView(){
        movieAdapter = PopularMovieAdapter(this@HomeFragment)
        binding.rvPopularMovies.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            addOnScrollListener(this@HomeFragment.scrollListener)
        }

        nowPlayingMovieAdapter = NowPlayingMovieAdapter(this@HomeFragment)
        binding.rvNowPlayingMovie.apply {
            adapter = nowPlayingMovieAdapter
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            addOnScrollListener(this@HomeFragment.scrollListnerNowPlaying)
        }

        upcomingMovieAdapter = UpcomingMovieAdapter(this@HomeFragment)
        binding.rvUpcomingMovie.apply {
            adapter = upcomingMovieAdapter
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            addOnScrollListener(this@HomeFragment.scrollListnerUpcoming)
        }
    }

    private fun init(){
        viewPager2 = binding.movieViewPager
        handler = Handler(Looper.myLooper()!!)
    }

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f

        }
        viewPager2.setPageTransformer(transformer)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 2000)
    }

    override fun onItemClickPM(movieData: MovieData) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(movieData)
        findNavController().navigate(action)
    }


    override fun onItemClickNP(movieData: MovieData) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(movieData)
        findNavController().navigate(action)
    }

    override fun onItemClickUC(movieData: MovieData) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(movieData)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}