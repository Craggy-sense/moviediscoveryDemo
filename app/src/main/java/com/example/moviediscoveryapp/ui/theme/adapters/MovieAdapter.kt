package com.example.moviediscovery.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviediscovery.data.models.Movie
import com.example.moviediscovery.databinding.ItemMovieBinding

class MovieAdapter(
    private var movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                tvTitle.text = movie.title
                tvRating.text = "Rating: ${movie.voteAverage}/10"
                tvReleaseDate.text = "Release: ${movie.releaseDate}"
                tvOverview.text = movie.overview.take(100) + "..."

                val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                Glide.with(ivPoster.context)
                    .load(posterUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(ivPoster)

                root.setOnClickListener {
                    onItemClick(movie)
                }
            }
        }
    }
}