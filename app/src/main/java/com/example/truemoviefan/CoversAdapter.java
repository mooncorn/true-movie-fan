package com.example.truemoviefan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Movie;

public class CoversAdapter extends RecyclerView.Adapter<CoversAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movies;

    public CoversAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public CoversAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View searchView = inflater.inflate(R.layout.item_cover, parent, false);

        // Return a new holder instance
        return new CoversAdapter.ViewHolder(searchView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull CoversAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Movie movie = movies.get(position);

        // Set item views based on your views and data model
        holder.tvMovieTitle.setText(movie.getTitle());

        // TODO: fetch number of reviews
        holder.tvMovieRating.setText("X");

        Picasso.with(context).load(movie.getPoster()).into(holder.ivMoviePoster);

        holder.ivMoviePoster.setOnClickListener(view -> {
            Intent i = new Intent(context, MovieActivity.class);
            i.putExtra(MovieActivity.IMDB_ID, movie.getImdbID());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // movie rating is number of reviews
        public TextView tvMovieTitle, tvMovieRating;
        public ImageView ivMoviePoster;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMovieTitle = (TextView) itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = (TextView) itemView.findViewById(R.id.tvMovieRating);
            ivMoviePoster = (ImageView) itemView.findViewById(R.id.ivMoviePoster);
        }
    }
}
