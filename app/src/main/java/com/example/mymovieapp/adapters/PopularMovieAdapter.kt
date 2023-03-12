package com.example.mymovieapp.adapters

import android.annotation.SuppressLint
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

class PopularMovieAdapter(private val listener: OnItemClickListenerPM):
    RecyclerView.Adapter<PopularMovieAdapter.PopularMovieViewHolder>() {

    inner class PopularMovieViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){
            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION) {
                        val item = differ.currentList[position]
                        listener.onItemClickPM(item)
                    }
                }
            }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<MovieData>() {
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
                R.layout.item_movie_preview, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PopularMovieViewHolder, position: Int) {
        val movie = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load("https://image.tmdb.org/t/p/w500"+ movie.poster_path).into(movieIV)
            movieTitleTv.text = movie.title
            releaseDateTv.text = "Release Date: ${movie.release_date}"
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListenerPM{
        fun onItemClickPM(movieData: MovieData)
    }
}