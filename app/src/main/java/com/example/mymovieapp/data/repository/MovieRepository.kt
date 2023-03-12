package com.example.mymovieapp.data.repository

import com.example.mymovieapp.data.network.RemoteDataSource
import com.example.mymovieapp.utils.Constants
import com.example.mymovieapp.utils.Constants.Companion.API_KEY

class MovieRepository() {

    suspend fun getPopularMovies(page: Int) =
        RemoteDataSource.api.getPopularMovie(page)

    suspend fun getTrendingMovies(page: Int) =
        RemoteDataSource.api.getTrendingMovies(page)

    suspend fun getNowPlayingMovies(page: Int) =
        RemoteDataSource.api.getNowPlayingMovies(page)

    suspend fun getUpcomingMovies(page: Int) =
        RemoteDataSource.api.getUpcomingMovies(page)

    suspend fun getSearchMovies(page: Int, searchKey: String) =
        RemoteDataSource.api.getSearchMovies(page, searchKey)

    suspend fun getMovieVideosDetails(movieId: String) =
        RemoteDataSource.api.getMovieVideoDetails("/3/movie/${movieId}/videos?api_key=${API_KEY}")

    suspend fun getMovieCastDetails(movieId: String) =
        RemoteDataSource.api.getMovieCastDetails("/3/movie/${movieId}/credits?api_key=${API_KEY}")
}