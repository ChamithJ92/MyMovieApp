package com.example.mymovieapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymovieapp.R
import com.example.mymovieapp.data.model.responses.MovieData
import kotlinx.android.synthetic.main.item_movie_preview.view.*
import kotlinx.android.synthetic.main.item_movie_preview.view.movieTitleTv
import kotlinx.android.synthetic.main.item_movie_preview.view.releaseDateTv
import kotlinx.android.synthetic.main.item_movie_preview_two.view.*

class PopularMovieListAdapter(private val listener: OnItemClickListener)
    : RecyclerView.Adapter<PopularMovieListAdapter.PopularMovieViewHolder>() {

    inner class PopularMovieViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = differ.currentList[position]
                    listener.onItemClick(item)
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<MovieData>(){
        override fun areItemsTheSame(oldItem: MovieData, newItem: MovieData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieData, newItem: MovieData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMovieViewHolder {
        return PopularMovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_search_movie_preview, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PopularMovieViewHolder, position: Int) {
        val movie = differ.currentList[position]

        holder.itemView.apply {
            if(movie.poster_path != null || movie.poster_path == "") {
                Glide.with(this).load("https://image.tmdb.org/t/p/w500" + movie.poster_path)
                    .into(movieImg)
            }else{
                Glide.with(this).load(R.drawable.no_image)
                    .into(movieImg)
            }
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

            var pStatus: Double = movie.vote_average!! * 10

            progressBar.progress = pStatus.toInt()
            txtProgress.text = "${pStatus.toInt()}%"
        }
    }

    interface OnItemClickListener{
        fun onItemClick(movieData: MovieData)
    }
}