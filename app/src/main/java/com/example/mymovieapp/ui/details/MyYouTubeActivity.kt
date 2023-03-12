package com.example.mymovieapp.ui.details

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.example.mymovieapp.R
import com.example.mymovieapp.utils.Constants
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class MyYouTubeActivity : YouTubeBaseActivity() {

    private lateinit var youTubePlayer: YouTubePlayerView
    private val args by navArgs<MyYouTubeActivityArgs>()

    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    var youTubeVideoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_you_tube)

        apply {
            youTubeVideoId = args.youTubeId
        }

        youTubePlayer = findViewById(R.id.myYouTube)

        youtubePlayerInit = object : YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1?.loadVideo(youTubeVideoId)
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(applicationContext, "Failed",Toast.LENGTH_SHORT).show()
            }
        }

        youTubePlayer.initialize(Constants.YOU_TUBE_API_KEY, youtubePlayerInit)
    }

}