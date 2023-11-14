package com.example.mymovieapp.ui.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.example.mymovieapp.MovieApplication
import com.example.mymovieapp.data.model.responses.*
import com.example.mymovieapp.data.repository.MovieRepository
import com.example.mymovieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MovieViewModel(app: Application,
                     val movieRepository: MovieRepository) : AndroidViewModel(app) {

    val trendingMovies: MutableLiveData<Resource<TrendingMovieResponse>> = MutableLiveData()
    var trendingMoviePage = 1
    var trendingMovieResponse: TrendingMovieResponse? = null

    val popularMovies: MutableLiveData<Resource<PopularMovieResponse>> = MutableLiveData()
    var popularMoviePage = 1
    var popularMovieResponse: PopularMovieResponse? = null

    val nowPlayingMovies: MutableLiveData<Resource<NowPlayingMovieResponse>> = MutableLiveData()
    var nowPlayingMoviePage = 1
    var nowPlayingResponse: NowPlayingMovieResponse? = null

    val upcomingMovies: MutableLiveData<Resource<UpcomingMovieResponse>> = MutableLiveData()
    var upcomingMoviePage = 1
    var upcomingMovieResponse: UpcomingMovieResponse? = null

    val searchMovies: MutableLiveData<Resource<SearchMovieResponse>> = MutableLiveData()
    var searchMoviePage = 1
    var searchMovieResponse: SearchMovieResponse? = null

    val movieVideos: MutableLiveData<Resource<MovieVideosResponse>> = MutableLiveData()
    var movieVideosResponse: MovieVideosResponse? = null

    val movieCast: MutableLiveData<Resource<MovieCastResponse>> = MutableLiveData()
    var movieCastResponse: MovieCastResponse? = null

    init {
        getTrendingMovies()
        getPopularMovies()
        getNowPlayingMovies()
        getUpcomingMovies()
    }

    fun getPopularMovies() = viewModelScope.launch {
        safePopularMoviesCall()
    }

    private fun handlePopularMovieResponse(response:
                                           Response<PopularMovieResponse>):
            Resource<PopularMovieResponse> {
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                popularMoviePage++
                Log.e("++++++++", ""+popularMoviePage)
                if(popularMovieResponse == null){
                    popularMovieResponse = resultResponse
                }else{
                    val oldMovies = popularMovieResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(popularMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safePopularMoviesCall(){
        popularMovies.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = movieRepository.getPopularMovies(popularMoviePage)
                popularMovies.postValue(handlePopularMovieResponse(response))
            }else{
                popularMovies.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> popularMovies.postValue(Resource.Error("Network Failure"))
                else -> popularMovies.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    /***-- Handling Trending Movies API --***/


    fun getTrendingMovies() = viewModelScope.launch {
        safeTrendingMoviesCall()
    }

    private fun handleTrendingMovieResponse(response: Response<TrendingMovieResponse>):
        Resource<TrendingMovieResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                trendingMoviePage++
                if(trendingMovieResponse == null){
                    trendingMovieResponse = resultResponse
                }else {
                    val oldMovies = trendingMovieResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(trendingMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeTrendingMoviesCall(){
        trendingMovies.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = movieRepository.getTrendingMovies(trendingMoviePage)
                trendingMovies.postValue(handleTrendingMovieResponse(response))
            }else{
                trendingMovies.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t) {
                is IOException -> trendingMovies.postValue(Resource.Error("Network Failure"))
                else -> trendingMovies.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    /***-- Handling Now Playing Movies API --***/

    fun getNowPlayingMovies() = viewModelScope.launch {
        safeNowPlayingMoviesCall()
    }

    private fun handleNowPlayingMovieResponse(response: Response<NowPlayingMovieResponse>):
        Resource<NowPlayingMovieResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                nowPlayingMoviePage++
                if(nowPlayingResponse == null){
                    nowPlayingResponse = resultResponse
                }else{
                    val oldMovies = nowPlayingResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(nowPlayingResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeNowPlayingMoviesCall(){
        nowPlayingMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = movieRepository.getNowPlayingMovies(nowPlayingMoviePage)
                nowPlayingMovies.postValue(handleNowPlayingMovieResponse(response))
            }else {
                nowPlayingMovies.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t) {
                is IOException -> trendingMovies.postValue(Resource.Error("Network Failure"))
                else -> trendingMovies.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    /***-- Handling Upcoming Movies API --***/

    fun getUpcomingMovies() = viewModelScope.launch {
        safeUpComingMovieCall()
    }

    private fun handleUpcomingMovieResponse(response: Response<UpcomingMovieResponse>):
            Resource<UpcomingMovieResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                upcomingMoviePage++
                if(upcomingMovieResponse == null){
                    upcomingMovieResponse = resultResponse
                }else{
                    val oldMovies = upcomingMovieResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(upcomingMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeUpComingMovieCall(){
        upcomingMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = movieRepository.getUpcomingMovies(upcomingMoviePage)
                upcomingMovies.postValue(handleUpcomingMovieResponse(response))
            }else {
                upcomingMovies.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> trendingMovies.postValue(Resource.Error("Network Failure"))
                else -> trendingMovies.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    /***-- Handling Search Movies API --***/

    fun getSearchMovies(searchKey: String) = viewModelScope.launch {
        safeSearchMovieCall(searchKey)
    }

    private fun handleSearchMovieResponse(response: Response<SearchMovieResponse>):
            Resource<SearchMovieResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchMoviePage++
                if(searchMovieResponse == null){
                    searchMovieResponse = resultResponse
                }else{
                    val oldMovies = searchMovieResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(searchMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchMovieCall(searchKey: String){
        searchMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = movieRepository.getSearchMovies(searchMoviePage, searchKey)
                searchMovies.postValue(handleSearchMovieResponse(response))
            }else{
                searchMovies.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchMovies.postValue(Resource.Error("Network Failure"))
                else -> searchMovies.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    /***-- Handling Movie Cast API --***/

    fun getMovieCast(movieId: String) = viewModelScope.launch {
        safeMovieCastCall(movieId)
    }

    private fun handleMovieCastResponse(response: Response<MovieCastResponse>):
            Resource<MovieCastResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                movieCastResponse = resultResponse
                return Resource.Success(movieCastResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeMovieCastCall(movieId: String){
        movieCast.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = movieRepository.getMovieCastDetails(movieId)
                movieCast.postValue(handleMovieCastResponse(response))
            }else{
                movieCast.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> movieCast.postValue(Resource.Error("Network Failure"))
                else -> movieCast.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    /***-- Handling Movie Video API --***/

    fun getMovieVideos(movieId: String) = viewModelScope.launch {
        safeMovieVideoCall(movieId)
    }

    private fun handleMovieVideoResponse(response: Response<MovieVideosResponse>):
            Resource<MovieVideosResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                movieVideosResponse = resultResponse
                return Resource.Success(movieVideosResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeMovieVideoCall(movieId: String){
        movieVideos.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = movieRepository.getMovieVideosDetails(movieId)
                movieVideos.postValue(handleMovieVideoResponse(response))
            }else{
                movieVideos.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> movieVideos.postValue(Resource.Error("Network Failure"))
                else -> movieVideos.postValue(Resource.Error("Connection Error"))
            }
        }
    }

    /***-- Handling Internet Connection --***/

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MovieApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}