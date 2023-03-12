package com.example.mymovieapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mymovieapp.R
import com.example.mymovieapp.data.model.responses.MovieData
import kotlinx.android.synthetic.main.item_movie_preview.view.*
import kotlinx.android.synthetic.main.item_movie_preview.view.movieTitleTv
import kotlinx.android.synthetic.main.item_movie_preview.view.releaseDateTv
import kotlinx.android.synthetic.main.item_movie_preview_two.view.*

class TrendingMovieAdapter(private val currentList: ArrayList<MovieData>,
                           private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<TrendingMovieAdapter.TrendingMovieViewHolder>(){

    inner class TrendingMovieViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingMovieViewHolder {
        return TrendingMovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_movie_preview_two, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TrendingMovieViewHolder, position: Int) {
        val movie = currentList[position]

        holder.itemView.apply {
            Glide.with(this).load("https://image.tmdb.org/t/p/w500"+ movie.poster_path).into(movieImg)

            var movieTitle: String = ""
            if(movie.original_name != null) {
                movieTitle = movie.original_name
            }else if(movie.title != null) {
                movieTitle = movie.title
            }else if(movie.original_name!!) {
                movieTitle = movie.original_name
            }

            if(movieTitle.length > 20) {
                movieTitleTv.text = "${movieTitle.substring(0, 20)}..."
            }else {
                movieTitleTv.text = movieTitle
            }

            var releaseDate: String = ""
            if(movie.first_air_date != null){
                releaseDate = movie.first_air_date
            }else if(movie.release_date != null){
                releaseDate = movie.release_date
            }

            releaseDateTv.text = "Release Date: ${releaseDate}"

            if(position == currentList.size - 1){
                viewPager2.post(runnable)
            }

            var pStatus: Double = movie.vote_average!! * 10

            progressBar.progress = pStatus.toInt()
            txtProgress.text = "${pStatus.toInt()}%"
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    private val runnable = Runnable {
        currentList.addAll(currentList)
        notifyDataSetChanged()
    }
}