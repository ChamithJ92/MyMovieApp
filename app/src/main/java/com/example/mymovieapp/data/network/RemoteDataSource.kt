package com.example.mymovieapp.data.network

import com.example.mymovieapp.BuildConfig
import com.example.mymovieapp.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource {
    companion object{

        private val buildApi by lazy {
            Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .also { client ->
                    if(BuildConfig.DEBUG){
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        }

        val api by lazy {
            buildApi.create(MovieApi::class.java)
        }
    }

//    fun <Api> buildApi(
//        api: Class<Api>
//    ): Api{
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .also { client ->
//                    if(BuildConfig.DEBUG){
//                        val logging = HttpLoggingInterceptor()
//                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//                        client.addInterceptor(logging)
//                    }
//                }.build())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(api)
//    }
}