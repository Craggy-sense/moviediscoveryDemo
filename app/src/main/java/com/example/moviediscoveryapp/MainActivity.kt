package com.example.moviediscoveryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.moviediscovery.ui.viewmodels.MainViewModel
import com.example.moviediscoveryapp.ui.screens.MovieScreen
import com.example.moviediscoveryapp.ui.theme.MovieDiscoveryAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity is the "Doorway" to your app.
 * After the Compose migration, this file became much shorter because the UI logic
 * moved to MovieScreen.kt and MovieItem.kt.
 *
 * We use @AndroidEntryPoint so Hilt can automatically inject the ViewModel.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // The ViewModel is the "Brain" of the app. 
    // We use 'by viewModels()' to get an instance that survives screen rotations.
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // setContent replaces the old 'setContentView(R.layout.activity_main)'.
        // It tells the Activity: "Use this Compose function to draw the screen."
        setContent {
            // MovieDiscoveryAppTheme is your app's global style (Colors, Fonts).
            MovieDiscoveryAppTheme {
                // Surface is the "Canvas" or background of your screen.
                Surface(
                    modifier = Modifier.fillMaxSize(), // Fill the entire phone screen
                    color = MaterialTheme.colorScheme.background // Use the theme's background color
                ) {
                    // We call our MovieScreen function and pass it the ViewModel.
                    MovieScreen(
                        viewModel = viewModel,
                        onMovieClick = { movie ->
                            // When a movie is clicked, we show the details pop-up.
                            showMovieDetails(movie)
                        }
                    )
                }
            }
        }
    }

    /**
     * This function creates a standard Android Pop-up (AlertDialog).
     * Even though we use Compose for the list, we can still use traditional 
     * Android tools for simple alerts.
     */
    private fun showMovieDetails(movie: com.example.moviediscovery.data.models.Movie) {
        android.app.AlertDialog.Builder(this)
            .setTitle(movie.title) // Show the movie name as the title
            .setMessage("""
                ⭐ Rating: ${movie.voteAverage}/10
                📅 Release Date: ${movie.releaseDate}
                🌐 Language: ${movie.originalLanguage.uppercase()}
                
                📖 Overview:
                ${movie.overview}
            """.trimIndent()) // String template to format the details nicely
            .setPositiveButton("OK", null) // Add a button to close the pop-up
            .show() // Actually display it to the user
    }
}