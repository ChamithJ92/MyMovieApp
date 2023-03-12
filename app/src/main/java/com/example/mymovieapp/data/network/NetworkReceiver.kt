package com.example.mymovieapp.data.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetworkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo

//        if (networkInfo != null && networkInfo.isConnected) {
//            Toast.makeText(context, "Internet is available", Toast.LENGTH_SHORT).show()
//
//        } else {
//            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
//        }

//        if (ConnectivityManager.CONNECTIVITY_ACTION == intent!!.action) {
//            val noConnectivity = intent.getBooleanExtra(
//                ConnectivityManager.EXTRA_NO_CONNECTIVITY, true
//            )
//            showInternetDisconnectedView(noConnectivity)
//        }
    }
}