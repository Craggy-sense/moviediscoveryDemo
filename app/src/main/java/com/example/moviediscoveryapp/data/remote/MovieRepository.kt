package com.example.moviediscovery.data.remote

import com.example.moviediscovery.BuildConfig
import com.example.moviediscovery.data.models.Movie
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MovieRepository is the "Single Source of Truth" for movie data in your app.
 * In the MVVM pattern, the Repository is like a "Pantry Manager."
 * The ViewModel asks the Repository for data, and the Repository decides where to get it 
 * (from the internet or a local database).
 *
 * @Singleton: Ensures only one instance of this repository exists in the entire app.
 * @Inject: Tells Hilt how to create this class and what "tools" (tmdbService) it needs.
 */
@Singleton
class MovieRepository @Inject constructor(
    private val tmdbService: TMDBService // The API service used to talk to TMDB
) {

    /**
     * Fetches a list of popular movies from the TMDB API.
     * 
     * suspend: This means the function can run in the background without freezing the UI.
     * @param page The page number to fetch (for infinite scrolling).
     * @return A list of Movie objects, or an empty list if there's an error.
     */
    suspend fun getPopularMovies(page: Int = 1): List<Movie> {
        return try {
            // We call the API and pass our secret API Key from BuildConfig
            val response = tmdbService.getPopularMovies(BuildConfig.TMDB_API_KEY, page)
            response.results // Return just the list of movies from the response
        } catch (e: Exception) {
            // If the internet is down or the API fails, return an empty list instead of crashing
            emptyList()
        }
    }

    /**
     * Fetches movies that are currently playing in theaters.
     */
    suspend fun getNowPlayingMovies(page: Int = 1): List<Movie> {
        return try {
            val response = tmdbService.getNowPlayingMovies(BuildConfig.TMDB_API_KEY, page)
            response.results
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Searches for movies based on a user's typed query (e.g., "Batman").
     * 
     * @param query The text the user typed in the search bar.
     */
    suspend fun searchMovies(query: String, page: Int = 1): List<Movie> {
        return try {
            val response = tmdbService.searchMovies(BuildConfig.TMDB_API_KEY, query, page)
            response.results
        } catch (e: Exception) {
            emptyList()
        }
    }
}