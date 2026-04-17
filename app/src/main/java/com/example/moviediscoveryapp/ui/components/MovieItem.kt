package com.example.moviediscoveryapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moviediscovery.data.models.Movie

/**
 * MovieItem is a "Composable" function. In Jetpack Compose, UI components are functions!
 * This replaces the old XML layout for a single movie row.
 *
 * @param movie The data object containing movie details (Title, Poster Path, etc.)
 * @param onClick A "Callback" function that runs when the user taps on this card.
 */
@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    // Card creates a nice container with rounded corners and a shadow (elevation)
    Card(
        modifier = Modifier
            .fillMaxWidth() // Make the card take the full width of the screen
            .padding(8.dp) // Add space outside the card
            .clickable { onClick() }, // Make the whole card clickable
        elevation = CardDefaults.cardElevation(4.dp) // This adds the "shadow" effect
    ) {
        // Row is like a horizontal LinearLayout - puts the Image and Text side-by-side
        Row(
            modifier = Modifier
                .padding(12.dp) // Space inside the card
                .fillMaxWidth()
        ) {
            // AsyncImage (from Coil library) downloads and displays the movie poster
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}", // Full URL to the image
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .size(80.dp, 120.dp), // Fixed size for the poster
                contentScale = ContentScale.Crop // Crop image to fill the 80x120 box perfectly
            )

            // Column is like a vertical LinearLayout - stacks the Text elements on top of each other
            Column(
                modifier = Modifier
                    .padding(start = 12.dp) // Gap between the poster and the text
                    .fillMaxWidth()
            ) {
                // Movie Title
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Movie Rating
                Text(
                    text = "Rating: ${movie.voteAverage}/10",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                // Release Date
                Text(
                    text = "Release: ${movie.releaseDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                // Spacer adds a small vertical gap before the overview text
                Spacer(modifier = Modifier.height(8.dp))
                
                // Movie Overview (Summary)
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3, // Limit to 3 lines so cards stay a consistent size
                    overflow = TextOverflow.Ellipsis // Add "..." if the text is longer than 3 lines
                )
            }
        }
    }
}