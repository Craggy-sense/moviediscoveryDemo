package com.example.moviediscovery.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviediscovery.data.models.Movie
import com.example.moviediscovery.data.remote.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MovieUiState is a "Sealed Class".
 * It represents the 3 possible states of our screen:
 * 1. Loading: We are waiting for data from the internet.
 * 2. Success: We have the list of movies.
 * 3. Error: Something went wrong (like no internet).
 * 
 * Using a sealed class is a "Best Practice" because it makes UI code very safe.
 */
sealed class MovieUiState {
    object Loading : MovieUiState()
    data class Success(val movies: List<Movie>) : MovieUiState()
    data class Error(val message: String) : MovieUiState()
}

/**
 * MainViewModel is the "Brain" of the application.
 * It handles the logic and stores the data so that it doesn't disappear 
 * if the user rotates their phone.
 * 
 * @HiltViewModel: Tells Hilt how to provide this ViewModel to the Activity.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MovieRepository // The Brain asks the "Pantry Manager" for data
) : ViewModel() {

    // _popularMoviesState is private (mutable) so only this class can change it.
    private val _popularMoviesState = MutableLiveData<MovieUiState>()
    // popularMoviesState is public (read-only) so the Activity can "Observe" it.
    val popularMoviesState: LiveData<MovieUiState> = _popularMoviesState

    private val _searchResultsState = MutableLiveData<MovieUiState>()
    val searchResultsState: LiveData<MovieUiState> = _searchResultsState

    private var currentPage = 1 // Keeps track of which page of movies we are on
    private var isLoading = false // Prevents the app from asking for the same data twice at once
    private val allMovies = mutableListOf<Movie>() // Stores all movies loaded so far

    // init block runs as soon as the ViewModel is created.
    init {
        loadPopularMovies() // Start loading movies immediately
    }

    /**
     * Fetches popular movies from the repository.
     * This handles the "Infinite Scroll" logic by loading one page at a time.
     */
    fun loadPopularMovies() {
        if (isLoading) return // If we are already loading, don't start again
        isLoading = true
        _popularMoviesState.value = MovieUiState.Loading // Tell the UI to show a spinner

        // viewModelScope.launch starts a "Coroutine" (Background task).
        // This ensures the app doesn't freeze while waiting for the internet.
        viewModelScope.launch {
            try {
                // Ask the repository for the next page of movies
                val movies = repository.getPopularMovies(currentPage)
                allMovies.addAll(movies) // Add the new movies to our existing list
                
                // Update the state to "Success" and send the updated list to the UI
                _popularMoviesState.value = MovieUiState.Success(allMovies)
                currentPage++ // Prepare to load the next page next time
                isLoading = false
            } catch (e: Exception) {
                // If something fails, tell the UI to show an error message
                _popularMoviesState.value = MovieUiState.Error(e.message ?: "Unknown error")
                isLoading = false
            }
        }
    }

    /**
     * Searches for movies based on a user's query.
     */
    fun searchMovies(query: String) {
        if (query.isEmpty()) {
            _searchResultsState.value = MovieUiState.Success(emptyList())
            return
        }

        _searchResultsState.value = MovieUiState.Loading

        viewModelScope.launch {
            try {
                val movies = repository.searchMovies(query)
                _searchResultsState.value = MovieUiState.Success(movies)
            } catch (e: Exception) {
                _searchResultsState.value = MovieUiState.Error(e.message ?: "Search failed")
            }
        }
    }
}