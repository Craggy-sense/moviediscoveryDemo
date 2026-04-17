package com.example.moviediscoveryapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviediscovery.data.models.Movie
import com.example.moviediscovery.ui.viewmodels.MainViewModel
import com.example.moviediscovery.ui.viewmodels.MovieUiState
import com.example.moviediscoveryapp.ui.components.MovieItem

/**
 * MovieScreen is the "Main Page" of your app.
 * It manages the search bar, the list of movies, and the loading/error states.
 * 
 * @param viewModel The "Brain" that provides the movie data.
 * @param onMovieClick What happens when a movie is tapped (usually shows a pop-up).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(viewModel: MainViewModel, onMovieClick: (Movie) -> Unit) {
    // 1. Observe State: We convert LiveData from the ViewModel into Compose "State".
    // Whenever the data in the ViewModel changes, these variables update automatically.
    val popularMoviesState by viewModel.popularMoviesState.observeAsState()
    val searchResultsState by viewModel.searchResultsState.observeAsState()
    
    // 2. Local State: 'remember' keeps the search text even if the screen redraws.
    var searchQuery by remember { mutableStateOf("") }
    
    // 3. Scroll State: Remembers where you are in the list.
    val listState = rememberLazyListState()

    // 4. Infinite Scroll Logic: Detects if we are near the bottom of the list.
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            // If the last visible item is near the end of the list (total - 5), return true.
            lastVisibleItem?.index != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 5
        }
    }

    // 5. Side Effect: When 'shouldLoadMore' becomes true, ask the ViewModel for more popular movies.
    // This only happens if the search bar is empty.
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && searchQuery.isEmpty()) {
            viewModel.loadPopularMovies()
        }
    }

    // 6. Scaffold: A standard Material Design layout structure (Top Bar + Content).
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Discovery") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { padding ->
        // Main container for everything below the Top Bar
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 7. Search Bar: Updates the searchQuery and triggers the search in ViewModel.
            TextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        viewModel.searchMovies(it) // Search as you type
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search movies...") },
                singleLine = true
            )

            // 8. Logic to decide which list to show: Popular or Search Results.
            val displayMovies = if (searchQuery.isNotEmpty()) {
                (searchResultsState as? MovieUiState.Success)?.movies ?: emptyList()
            } else {
                (popularMoviesState as? MovieUiState.Success)?.movies ?: emptyList()
            }

            // 9. Content Area: Can show a Spinner, a List, or an Error.
            Box(modifier = Modifier.fillMaxSize()) {
                
                // Show a big center spinner if we have NO data and we are currently loading.
                if (displayMovies.isEmpty() && (popularMoviesState is MovieUiState.Loading || searchResultsState is MovieUiState.Loading)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    // 10. LazyColumn: The Compose version of RecyclerView.
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Loop through each movie and draw a MovieItem
                        items(displayMovies) { movie ->
                            MovieItem(movie = movie, onClick = { onMovieClick(movie) })
                        }
                        
                        // If we are loading more popular movies at the bottom, show a small spinner.
                        if (popularMoviesState is MovieUiState.Loading && searchQuery.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }

                // 11. Error Handling: Show a red message if something goes wrong.
                if (popularMoviesState is MovieUiState.Error && displayMovies.isEmpty()) {
                    Text(
                        text = (popularMoviesState as MovieUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}