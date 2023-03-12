package com.example.mymovieapp.data.model.responses

data class MovieCastResponse(
    val cast: List<Cast>,
    val crew: List<Crew>,
    val id: Int
)