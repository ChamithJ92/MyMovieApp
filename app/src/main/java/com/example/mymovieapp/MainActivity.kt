package com.example.mymovieapp

import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.example.mymovieapp.data.network.NetworkReceiver
import com.example.mymovieapp.data.repository.MovieRepository
import com.example.mymovieapp.databinding.ActivityMainBinding
import com.example.mymovieapp.ui.home.MovieViewModel
import com.example.mymovieapp.ui.home.MovieViewModelProviderFactory
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    lateinit var movieViewModel: MovieViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(NetworkReceiver(), intentFilter)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.HomeFragment
            ), drawerLayout
        )

        setSupportActionBar(binding.toolbar)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val movieRepository = MovieRepository()
        val viewModelProviderFactory = MovieViewModelProviderFactory(application, movieRepository)
        movieViewModel = ViewModelProvider(this, viewModelProviderFactory).get(MovieViewModel::class.java)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

 //   var doubleBackToExitPressedOnce = false
//
//    override fun onBackPressed() {
//        //val count = supportFragmentManager.backStackEntryCount
//       // if(count == 1) {
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed()
//                return
//            }
//            doubleBackToExitPressedOnce = true
//            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
//            Handler(Looper.getMainLooper()).postDelayed(Runnable {
//                doubleBackToExitPressedOnce = false
//            }, 2000)
////        }else{
////            supportFragmentManager.popBackStack()
////        }
//    }

    private val listner = NavController.OnDestinationChangedListener{controller, destination, arguments ->
        if (destination.id == R.id.HomeFragment){
        }
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listner)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listner)
    }
}