package com.example.mymovieapp.ui.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymovieapp.MainActivity
import com.example.mymovieapp.R
import com.example.mymovieapp.adapters.MovieCastAdapter
import com.example.mymovieapp.adapters.PopularMovieAdapter
import com.example.mymovieapp.databinding.FragmentDetailsBinding
import com.example.mymovieapp.ui.home.MovieViewModel
import com.example.mymovieapp.utils.Constants
import com.example.mymovieapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.item_movie_preview.view.*
import kotlinx.android.synthetic.main.item_movie_preview_two.view.*

class DetailsFragment : Fragment() {

    private val args by navArgs<DetailsFragmentArgs>()
    lateinit var viewModel: MovieViewModel

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    lateinit var castAdapter: MovieCastAdapter

    lateinit var youTubeId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).movieViewModel

        binding.apply {
            val movieData = args.movieData

            getMovieVideos(movieData.id.toString())
            getMovieCast(movieData.id.toString())

            Glide.with(this@DetailsFragment)
                .load("https://image.tmdb.org/t/p/w500"+movieData.backdrop_path)
                .error(R.drawable.no_image)
                .into(movieIV)

            var pStatus: Double = movieData.vote_average!! * 10

            progressBar.progress = pStatus.toInt()
            txtProgress.text = "${pStatus.toInt()}%"

            var movieTitle: String = ""
            if(movieData.original_name != null) {
                movieTitle = movieData.original_name
            }else if(movieData.title != null) {
                movieTitle = movieData.title
            }else if(movieData.original_name!!) {
                movieTitle = movieData.original_name
            }

            movieTitleTv.text = movieTitle

            var releaseDate: String = ""
            if(movieData.first_air_date != null){
                releaseDate = movieData.first_air_date
            }else if(movieData.release_date != null){
                releaseDate = movieData.release_date
            }

            releaseDateTv.text = "Release Date: ${releaseDate}"

            movieOverViewTv.text = movieData.overview
        }

        setRecyclerView()

        viewModel.movieVideos.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieVideoResponse ->
                        Log.e("VIDEO", "" + movieVideoResponse.results)
                        if(movieVideoResponse.results.size != 0) {
                            Log.e("EEEEEEEE", "ERRORRRRRRfdgfdhgfhfg")
                            Log.e("VIDEO", "" + movieVideoResponse.results[0].key.toString())
                            youTubeId = movieVideoResponse.results[0].key
                        }else {
                            Log.e("EEEEEEEE", "ERRORRRRRR")
                            youTubeId = ""
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

        binding.btnYouTube.setOnClickListener {
            if (youTubeId != ""){
                val action =
                    DetailsFragmentDirections.actionDetailsFragmentToMyYouTubeActivity(youTubeId)
                findNavController().navigate(action)
            }else{
                Toast.makeText(requireContext(), "Sorry. Video not available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.movieCast.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { castResponse ->
                        Log.e("=====", ""+ castResponse.cast)
                        castAdapter.differ.submitList(castResponse.cast.toList())
//                        val totalPages = castResponse.total_results / Constants.QUERY_PAGE_SIZE + 2
//                        isLastPageUp = viewModel.upcomingMoviePage == totalPages
//                        if(isLastPageUp){
//                            binding.rvUpcomingMovie.setPadding(0, 0, 0, 0)
//                        }
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

    private fun getMovieVideos(movieId: String){
        viewModel.getMovieVideos(movieId)
    }

    private fun getMovieCast(movieId: String){
        viewModel.getMovieCast(movieId)
    }

    private fun hideProgressBar(){
        binding.progressBarLoading.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.progressBarLoading.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youTubeId = ""
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubeId = ""
    }

//    var isLoading = false
//    var isLastPage = false
//    var isScrolling = false
//
//    val scrollListner = object : RecyclerView.OnScrollListener(){
//
//        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//            super.onScrollStateChanged(recyclerView, newState)
//            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                isScrolling = true
//            }
//        }
//
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//            val visibleItemCount = layoutManager.childCount
//            val totalItemCount = layoutManager.itemCount
//
//            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
//            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
//            val isNotAtBeginning = firstVisibleItemPosition >= 0
//            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
//            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
//                    && isTotalMoreThanVisible && isScrolling
//            if(shouldPaginate){
//                viewModel.getMovieCast()
//                isScrolling = false
//            }
//        }
//    }

    private fun setRecyclerView() {
        castAdapter = MovieCastAdapter()
        binding.rvSeriesCast.apply {
            adapter = castAdapter
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
//            addOnScrollListener(this@DetailsFragment.scrollListner)
        }
    }
}