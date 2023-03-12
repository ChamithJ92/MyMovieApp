package com.example.mymovieapp.data.model.responses

data class MovieVideosResponse(
    val id: Int,
    val results: MutableList<VideoResponse>
)