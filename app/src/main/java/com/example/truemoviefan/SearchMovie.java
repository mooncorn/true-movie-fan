package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;

import Api.MovieApiClient;
import Model.Movie;

public class SearchMovie extends AppCompatActivity {

    RecyclerView rvMovies;
    SearchView svSearchView;

    MovieApiClient movieClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);
        initialize();
    }

    private void initialize() {
        rvMovies = findViewById(R.id.rvMovies);

        // Search View
        svSearchView = findViewById(R.id.svSearchView);
        loadSearchView();
    }

    private void loadSearchView() {
        svSearchView.clearFocus();
        svSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Fetch information about the title
                movieClient.search(newText, new MovieApiClient.MovieApiClientCallback<List<Movie>>() {
                    @Override
                    public void success(List<Movie> data) {
                        displayMovie(data);
                    }

                    @Override
                    public void error(String message) {
                        if (message != null) {
                            Toast.makeText(SearchMovie.this, message, Toast.LENGTH_SHORT).show();
                        }

                        Intent i = new Intent(SearchMovie.this, MainActivity.class);
                        startActivity(i);
                    }
                });

                return true;
            }
        });
    }

    private void displayMovie(List<Movie> data) {
        SearchAdapter adapter = new SearchAdapter(data);
        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(SearchMovie.this));
    }
}