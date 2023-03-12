package com.example.mymovieapp.data.model.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchMovieResponse(
    val page: Int,
    val results: MutableList<MovieData>,
    val total_pages: Int,
    val total_results: Int,

    var searchKey: String? = null
): Parcelable
