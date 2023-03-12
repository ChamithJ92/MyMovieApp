package com.example.mymovieapp.data.network

import com.example.mymovieapp.data.model.responses.*
import com.example.mymovieapp.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface MovieApi {

    @GET("/3/movie/popular")
    suspend fun getPopularMovie(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY
    ) : Response<PopularMovieResponse>

    @GET("/3/trending/all/day")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<TrendingMovieResponse>

    @GET("/3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<NowPlayingMovieResponse>

    @GET("/3/movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<UpcomingMovieResponse>

    @GET("/3/search/movie")
    suspend fun getSearchMovies(
        @Query("page") page: Int = 1,
        @Query("query") searchKey: String,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<SearchMovieResponse>

    @GET("/3/movie/{movieId}")
    suspend fun getMovieDetails(
        @Path (value = "movie_id", encoded = true) movieId: String,
        @Query("api_key") apiKey: String = API_KEY,
    ): Response<SearchMovieResponse>

//    @GET("/3/movie/640146/videos")
//    suspend fun getMovieVideoDetails(
//        @Query("api_key") apiKey: String = API_KEY,
//    ): Response<MovieVideosResponse>

    @GET
    suspend fun getMovieVideoDetails(
        @Url url: String
    ): Response<MovieVideosResponse>

    @GET
    suspend fun getMovieCastDetails(
        @Url url: String
    ): Response<MovieCastResponse>
}