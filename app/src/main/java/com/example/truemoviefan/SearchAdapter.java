package com.example.truemoviefan;

import android.content.Context;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private List<Movie> movies;

    public SearchAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View searchView = inflater.inflate(R.layout.item_search, parent, false);

        // Return a new holder instance
        return new SearchAdapter.ViewHolder(searchView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Movie movie = movies.get(position);

        // Set item views based on your views and data model
        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMoviePlot.setText(movie.getPlot());
        Picasso.with(context).load(movie.getPoster()).into(holder.ivMoviePoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMovieTitle, tvMovieRuntime, tvMovieRating, tvMoviePlot;
        public ImageView ivMoviePoster;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMovieTitle = (TextView) itemView.findViewById(R.id.tvMovieTitle);
            tvMoviePlot = (TextView) itemView.findViewById(R.id.tvMoviePlot);
            ivMoviePoster = (ImageView) itemView.findViewById(R.id.ivMoviePoster);
        }
    }
}
