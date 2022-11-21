package com.example.truemoviefan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import Models.Movie;
import Services.MovieApiClient;

/**
 * Displays information of a movie. IMDB ID is received from intent.
 */
public class MovieActivity extends AppCompatActivity {
    public static final String IMDB_ID = "IMDB_ID";

    TextView txtContent;

    MovieApiClient movieClient;
    String imdbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        initialize();
    }

    private void initialize() {
        txtContent = findViewById(R.id.txtContent);

        movieClient = new MovieApiClient(this);

        // Receive id of title
        imdbId = getIntent().getStringExtra(IMDB_ID);

        // Fetch information about the title
        movieClient.findMovie(imdbId, new MovieApiClient.MovieApiClientCallback<Movie>() {
            @Override
            public void success(Movie data) {
                txtContent.setText(data.toString());
            }

            @Override
            public void error(String message) {
                txtContent.setText(message);
            }
        });
    }
}