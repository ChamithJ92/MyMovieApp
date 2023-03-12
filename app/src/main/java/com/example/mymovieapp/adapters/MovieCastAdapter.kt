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
import com.example.mymovieapp.data.model.responses.Cast
import kotlinx.android.synthetic.main.item_cast_preview.view.*
import kotlinx.android.synthetic.main.item_movie_preview.view.*

class MovieCastAdapter:
    RecyclerView.Adapter<MovieCastAdapter.MovieCastViewHolder>() {

    inner class MovieCastViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCastViewHolder {
        return MovieCastViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_cast_preview, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MovieCastViewHolder, position: Int) {
        val cast = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load("https://image.tmdb.org/t/p/w500"+ cast.profile_path)
            .error(R.drawable.no_image).into(castIV)

            var name: String = ""
            if(cast.original_name != null){
                name = cast.original_name
            }else if(cast.name != null){
                name = cast.name
            }
            castNameTv.text = name

            var character: String = ""
            if(cast.character != null){
                character = cast.character
            }
            characterTv.text = character
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}